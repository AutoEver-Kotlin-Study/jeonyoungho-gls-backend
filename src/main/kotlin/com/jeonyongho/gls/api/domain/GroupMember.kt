package com.jeonyongho.gls.api.domain

import java.time.LocalDateTime

class GroupMember(
    val id: Long?,
    val groupId: Long,
    val userId: Long,
    val joinedAt: LocalDateTime,
) {
    companion object {
        fun create(groupId: Long, userId: Long): GroupMember = GroupMember(
            id = 0,
            groupId = groupId,
            userId = userId,
            joinedAt = LocalDateTime.now(),
        )
    }
}