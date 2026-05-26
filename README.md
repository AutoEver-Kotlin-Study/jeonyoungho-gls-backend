# 📍 [코틀린 스터디] 그룹 위치 공유 데모 애플리케이션 개발 실습

## 도메인 모델 설계
<img width="1106" height="544" alt="image" src="https://github.com/user-attachments/assets/93adc9c3-f38b-4efe-9e9b-66a792b2b8cb" />


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
