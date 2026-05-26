package com.jeonyongho.gls.api.adapter.outbound.persistence

import com.jeonyongho.gls.api.domain.Group
import jakarta.persistence.*

@Entity
@Table(name = "groups")
class GroupJpaEntity(
    val name: String,
    val maxMemberCount: Int,
    val ownerId: Long,

    /**
     *  @Id를 가장 하위에 두는걸 코틀린 + DDD + 헥사고날 아키텍처에서 더 선호
     *  - id는 JPA가 관리하는 값이라 생성 시 직접 넘기지 않는 경우가 많음
     *  - 생성자에서 “실제 비즈니스 값”이 먼저 보이게 됨
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseTimeEntity() {
    fun toDomain(): Group = Group(
        id = id,
        name = name,
        maxMemberCount = maxMemberCount,
        ownerId = ownerId,
    )

    companion object {
        fun from(group: Group): GroupJpaEntity = GroupJpaEntity(
            id = group.id,
            name = group.name,
            maxMemberCount = group.maxMemberCount,
            ownerId = group.ownerId,
        )
    }
}