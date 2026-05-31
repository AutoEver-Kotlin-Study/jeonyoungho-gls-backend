# 📍 [코틀린 스터디] 그룹 위치 공유 데모 애플리케이션 개발 실습

## 프로젝트 구조 및 아키텍처
- DDD + Hexagonal Architecture


## 기술 스택
- **언어**: Kotlin
- **프레임워크**: Spring Boot
- **데이터베이스**: InMemory H2
- **빌드 도구**: Gradle

> **참고**: 해당 프로젝트는 학습 목적으로 설계된 데모 애플리케이션이기에 Redis 와 Kafka 와 같은 미들웨어들은 사용하지 못하는 제약사항을 두었습니다.


## 도메인 모델 설계
<img width="1106" height="544" alt="image" src="https://github.com/user-attachments/assets/93adc9c3-f38b-4efe-9e9b-66a792b2b8cb" />

- Group 과 User 는 N:M 관계입니다. (하나의 그룹에 여러 명이 참여할 수 있고, 하나의 사용자가 여러 그룹에 참여할 수 있음)
- JPA 기준으로 1:N / N:1 관계로 모델링하기 위해 GroupMember 엔티티를 추가하여 Group 과 User 사이의 관계를 표현합니다.
- 한 유저는 여러 그룹에 속할 수 있다는 전제하에, Location 은 User 모델과 1:N 관계로 모델링됩니다.

## 동시성 문제
- 그룹 참여/퇴장시 동시성 문제를 해결하기 위해 다음과 같은 고민을 하였습니다.
- 데모 애플리케이션은 동시요청이 많으며, Redis 또한 제한적인 환경으로 가정하였습니다.
- **따라서 비관적/낙관적락보단 애플리케이션 레벨에서 최대 멤버 수 초과 문제를 해결하는 방식을 선택하였습니다.**
- 그룹 참여시 그룹의 현재 멤버 수를 1 증가시키는 쿼리를 실행하여, 만약 업데이트된 행이 없을 경우 예외를 던지도록 구현하였습니다.

```kotlin
// == 그룹 참여 시나리오 ==
// GroupService
@Transactional
fun joinGroup(command: JoinGroupCommand) {
  // ...
  val updated = groupOutPort.increaseCurrentMemberCount(command.groupId)
  if (updated == 0) {
    throw DomainException(ErrorCode.GROUP_FULL, "groupId=${command.groupId}")
  }
  // ...
}

// GroupJpaRepository
interface GroupJpaRepository : JpaRepository<GroupJpaEntity, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
          UPDATE GroupJpaEntity g 
          SET g.currentMemberCount = g.currentMemberCount + 1
          WHERE g.id = :groupId 
          AND g.currentMemberCount < g.maxMemberCount
      """
    )
    fun increaseCurrentMemberCount(groupId: Long): Int
}
```

- 그룹 퇴장시 그룹 멤버 삭제 쿼리를 실행하면서, 실제로 삭제된 행이 없는 경우 예외를 던지도록 구현하였습니다.

```kotlin
// == 그룹 퇴장 시나리오 ==
// GroupService
@Transactional
fun leaveGroup(command: LeaveGroupCommand) {
    // ...
    val deleted = groupMemberOutPort.deleteByGroupIdAndUserId(command.groupId, command.userId)
    if (deleted == 0) {
        throw DomainException(
            ErrorCode.GROUP_MEMBER_NOT_FOUND,
            "groupId=${command.groupId}, userId=${command.userId}"
        )
    }
    // ...
}

// GroupMemberJpaRepository
interface GroupMemberJpaRepository : JpaRepository<GroupMemberJpaEntity, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM GroupMemberJpaEntity gm WHERE gm.groupId = :groupId AND gm.userId = :userId")
    fun deleteByGroupIdAndUserId(groupId: Long, userId: Long): Int
}
```

## SMS 알림
- Spring 이벤트 기반 비동기 처리를 활용하여, 그룹 참여/퇴장 시 SMS 알림을 보내는 기능을 구현하였습니다.
- 그룹 참여/퇴장 시점에 `GroupJoinedEvent` / `GroupLeftEvent` 이벤트가 발행되며, 이를 처리하는 `GroupEventListener` 클래스에서 SMS 알림 로직이 실행됩니다.
- **알림 발송은 별도 비동기 스레드풀(`groupSmsExecutor`)을 분리하여 처리되도록 구현하여, SMS 서버 장애시에도 다른 외부 API 호출이나 DB 트랜잭션에 영향을 주지 않도록 설계하였습니다.**


## API 명세

### 공통 사항
- **Base URL**: `/groups`
- **공통 Header**: `X-User-Id` (사용자 ID, 필수)
- **응답 형식**: JSON

---

### 그룹(Group) API

#### 1. 그룹 생성
- **endpoint**: `POST /groups`
- **description**: 새로운 그룹을 생성합니다.
- **request body**:
  ```json
  {
    "name": "string (필수, 공백 불가)",
    "maxMemberCount": "integer (필수, 1 이상)"
  }
  ```
- **response body**:
  ```json
  {
    "contents": {
      "id": "long"
    }
  }
  ```
- **Example (cURL)**:
  ```bash
  curl -X POST http://localhost:8080/groups \
    -H "X-User-Id: 1" \
    -H "Content-Type: application/json" \
    -d '{
      "name": "강남역 만남",
      "maxMemberCount": 5
    }'
  ```

#### 2. 소속된 그룹 목록 조회
- **endpoint**: `GET /groups`
- **description**: 사용자가 속한 모든 그룹 목록을 조회합니다.
- **request body**: 없음
- **response body**:
  ```json
  {
    "contents": [
      {
        "groupId": "long",
        "name": "string",
        "maxMemberCount": "integer",
        "memberCount": "long"
      }
    ]
  }
  ```
- **Example (cURL)**:
  ```bash
  curl -X GET http://localhost:8080/groups \
    -H "X-User-Id: 1"
  ```

#### 3. 그룹 참여
- **endpoint**: `POST /groups/{groupId}/join`
- **description**: 특정 그룹에 참여합니다.
- **path variable**:
  - `groupId`: 그룹 ID (필수)
- **request body**: 없음
- **response body**:
  ```json
  {
    "contents": null
  }
  ```
- **Example (cURL)**:
  ```bash
  curl -X POST http://localhost:8080/groups/1/join \
    -H "X-User-Id: 2"
  ```

#### 4. (참여자) 그룹 퇴장
- **endpoint**: `DELETE /groups/{groupId}/leave`
- **description**: 특정 그룹에서 탈출합니다.
- **path variable**:
  - `groupId`: 그룹 ID (필수)
- **request body**: 없음
- **response body**:
  ```json
  {
    "contents": null
  }
  ```
- **Example (cURL)**:
  ```bash
  curl -X DELETE http://localhost:8080/groups/1/leave \
    -H "X-User-Id: 2"
  ```

#### 5. 그룹 삭제
- **endpoint**: `DELETE /groups/{groupId}`
- **description**: 특정 그룹을 삭제합니다. (그룹 소유자만 가능)
- **path variable**:
  - `groupId`: 그룹 ID (필수)
- **request body**: 없음
- **response body**:
  ```json
  {
    "contents": null
  }
  ```
- **Example (cURL)**:
  ```bash
  curl -X DELETE http://localhost:8080/groups/1 \
    -H "X-User-Id: 1"
  ```

---

### 위치(Location) API

#### 1. 그룹 위치 업데이트
- **endpoint**: `PUT /groups/{groupId}/location`
- **description**: 특정 그룹에 사용자의 현재 위치를 업데이트합니다.
- **path variable**:
  - `groupId`: 그룹 ID (필수)
- **request body**:
  ```json
  {
    "locations": [
      {
        "latitude": "double (필수)",
        "longitude": "double (필수)"
      }
    ]
  }
  ```
- **response body**:
  ```json
  {
    "contents": null
  }
  ```
- **Example (cURL)**:
  ```bash
  curl -X PUT http://localhost:8080/groups/1/location \
    -H "X-User-Id: 1" \
    -H "Content-Type: application/json" \
    -d '{
      "locations": [
        {
          "latitude": 37.4979,
          "longitude": 127.0276
        }
      ]
    }'
  ```

#### 2. 그룹 위치 조회 API
- **endpoint**: `GET /groups/{groupId}/locations`
- **description**: 특정 그룹내 모든 사용자의 현재 위치를 조회합니다.
- **path variable**:
  - `groupId`: 그룹 ID (필수)
- **request body**: 없음
- **response body**:
  ```json
  {
    "contents": [
      {
        "userId": "long",
        "userName": "string",
        "latitude": "double",
        "longitude": "double",
        "recordedAt": "datetime (ISO 8601)"
      }
    ]
  }
  ```
- **Example (cURL)**:
```bash
curl -X GET http://localhost:8080/groups/1/locations \
  -H "X-User-Id: 1"
```

## 애플리케이션 실행 및 활용 가이드
- 프로젝트 루트 경로에서 하기 명령어 실행

```bash
./gradlew bootRun
```

- misc/gls-backend-api.http 파일 기준 API 실행 (IntelliJ 또는 VSCode REST Client Extension 활용)
