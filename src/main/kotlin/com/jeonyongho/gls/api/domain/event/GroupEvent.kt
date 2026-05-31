package com.jeonyongho.gls.api.domain.event

import org.springframework.context.ApplicationEvent

/**
 * 그룹 멤버 참여 이벤트
 */
data class GroupMemberJoinedEvent(
    val groupId: Long,
    val joinedUserId: Long,
) : ApplicationEvent(groupId)

/**
 * 그룹 멤버 퇴장 이벤트
 */
data class GroupMemberLeftEvent(
    val groupId: Long,
    val leftMemberId: Long,
) : ApplicationEvent(groupId)

