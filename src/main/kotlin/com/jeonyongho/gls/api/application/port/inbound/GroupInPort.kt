package com.jeonyongho.gls.api.application.port.inbound


interface CreateGroupUseCase {
    fun createGroup(command: CreateGroupCommand): CreateGroupResult
}

interface GetMyGroupsUseCase {
    fun getMyGroups(query: GetMyGroupsQuery): List<GetMyGroupsResult>
}

interface JoinGroupUseCase {
    fun joinGroup(command: JoinGroupCommand)
}

interface LeaveGroupUseCase {
    fun leaveGroup(command: LeaveGroupCommand)
}

interface DeleteGroupUseCase {
    fun deleteGroup(command: DeleteGroupCommand)
}
