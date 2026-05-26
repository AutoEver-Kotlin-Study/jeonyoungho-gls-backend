package com.jeonyongho.gls.api.adapter.outbound.persistence.groupmember

import com.jeonyongho.gls.api.adapter.outbound.persistence.BaseTimeEntity
import com.jeonyongho.gls.api.domain.GroupMember
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "group_members",
    uniqueConstraints = [UniqueConstraint(columnNames = ["group_id", "user_id"])],
)
class GroupMemberJpaEntity(
    @Column(name = "group_id")
    val groupId: Long,

    @Column(name = "user_id")
    val userId: Long,

    val joinedAt: LocalDateTime,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseTimeEntity() {
    fun toDomain(): GroupMember = GroupMember(
        id = id,
        groupId = groupId,
        userId = userId,
        joinedAt = joinedAt,
    )

    companion object {
        fun from(groupMember: GroupMember): GroupMemberJpaEntity = GroupMemberJpaEntity(
            id = groupMember.id,
            groupId = groupMember.groupId,
            userId = groupMember.userId,
            joinedAt = groupMember.joinedAt,
        )
    }
}