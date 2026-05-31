package com.jeonyongho.gls.api.application.port.inbound

data class CreateGroupCommand(
    val userId: Long,
    val name: String,
    val maxMemberCount: Int,
)

data class CreateGroupResult(
    val groupId: Long,
)

data class GetMyGroupsQuery(val userId: Long)

data class GetMyGroupsResult(
    val groupId: Long,
    val name: String,
    val maxMemberCount: Int,
    val memberCount: Int,
)

data class JoinGroupCommand(
    val groupId: Long,
    val userId: Long,
)

data class LeaveGroupCommand(
    val groupId: Long,
    val userId: Long,
)

data class DeleteGroupCommand(
    val groupId: Long,
    val userId: Long,
)
