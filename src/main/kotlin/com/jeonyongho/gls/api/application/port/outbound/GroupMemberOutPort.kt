package com.jeonyongho.gls.api.application.port.outbound

import com.jeonyongho.gls.api.domain.GroupMember

interface GroupMemberOutPort {
    fun save(groupMember: GroupMember): GroupMember
    fun findByGroupIdAndUserId(groupId: Long, userId: Long): GroupMember?
    fun findAllByGroupId(groupId: Long): List<GroupMember>
    fun findAllByUserId(userId: Long): List<GroupMember>
    fun existsByGroupIdAndUserId(groupId: Long, userId: Long): Boolean
    fun countByGroupId(groupId: Long): Long
    fun deleteByGroupIdAndUserId(groupId: Long, userId: Long)
    fun deleteAllByGroupId(groupId: Long)
}
