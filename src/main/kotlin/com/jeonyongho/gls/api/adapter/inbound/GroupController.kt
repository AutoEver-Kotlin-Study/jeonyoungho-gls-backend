package com.jeonyongho.gls.api.adapter.inbound


import com.jeonyongho.gls.api.adapter.inbound.rqrs.CreateGroupRq
import com.jeonyongho.gls.api.adapter.inbound.rqrs.CreateGroupRs
import com.jeonyongho.gls.api.adapter.inbound.rqrs.GetMyGroupsRs
import com.jeonyongho.gls.api.application.port.inbound.*
import com.jeonyongho.gls.api.exceptions.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController (
    private val createGroupUseCase: CreateGroupUseCase,
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val joinGroupUseCase: JoinGroupUseCase,
    private val leaveGroupUseCase: LeaveGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
) {

    @PostMapping
    fun createGroup(
        @RequestHeader("X-User-Id") userId: Long,
        @Valid @RequestBody rq: CreateGroupRq, ): ApiResponse.Success<CreateGroupRs> {
        val result = createGroupUseCase.createGroup(
            CreateGroupCommand(
                userId = userId,
                name = rq.name,
                maxMemberCount = rq.maxMemberCount,
            )
        )
        return ApiResponse.success(CreateGroupRs.from(result))
    }

    @GetMapping
    fun getMyGroups(
        @RequestHeader("X-User-Id") userId: Long,
    ): ApiResponse.Success<List<GetMyGroupsRs>> {
        val result = getMyGroupsUseCase.getMyGroups(GetMyGroupsQuery(userId))
        return ApiResponse.success(result.map { GetMyGroupsRs.from(it) })
    }

    @PostMapping("/{groupId}/join")
    fun joinGroup(
        @RequestHeader("X-User-Id") userId: Long,
        @PathVariable groupId: Long,
    ): ApiResponse.Success<Unit> {
        joinGroupUseCase.joinGroup(JoinGroupCommand(groupId = groupId, userId = userId))
        return ApiResponse.success(Unit)
    }

    @DeleteMapping("/{groupId}/leave")
    fun leaveGroup(
        @RequestHeader("X-User-Id") userId: Long,
        @PathVariable groupId: Long,
    ): ApiResponse.Success<Unit> {
        leaveGroupUseCase.leaveGroup(LeaveGroupCommand(groupId = groupId, userId = userId))
        return ApiResponse.success(Unit)
    }

    @DeleteMapping("/{groupId}")
    fun deleteGroup(
        @RequestHeader("X-User-Id") userId: Long,
        @PathVariable groupId: Long,
    ): ApiResponse.Success<Unit> {
        deleteGroupUseCase.deleteGroup(DeleteGroupCommand(groupId = groupId, userId = userId))
        return ApiResponse.success(Unit)
    }
}