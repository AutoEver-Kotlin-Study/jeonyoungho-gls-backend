package com.jeonyongho.gls.api.application.service

import com.jeonyongho.gls.api.application.port.inbound.*
import com.jeonyongho.gls.api.application.port.outbound.GroupMemberOutPort
import com.jeonyongho.gls.api.application.port.outbound.GroupOutPort
import com.jeonyongho.gls.api.application.port.outbound.UserOutPort
import com.jeonyongho.gls.api.domain.Group
import com.jeonyongho.gls.api.domain.GroupMember
import com.jeonyongho.gls.api.domain.event.GroupMemberJoinedEvent
import com.jeonyongho.gls.api.domain.event.GroupMemberLeftEvent
import com.jeonyongho.gls.api.exceptions.DomainException
import com.jeonyongho.gls.api.exceptions.ErrorCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GroupService(
    private val groupOutPort: GroupOutPort,
    private val groupMemberOutPort: GroupMemberOutPort,
    private val userOutPort: UserOutPort,
    private val eventPublisher: ApplicationEventPublisher,
) : CreateGroupUseCase, GetMyGroupsUseCase, JoinGroupUseCase, LeaveGroupUseCase, DeleteGroupUseCase {

    @Transactional
    override fun createGroup(command: CreateGroupCommand): CreateGroupResult {
        val user = userOutPort.findById(command.userId) ?: throw DomainException(
            ErrorCode.USER_NOT_FOUND,
            "사용자를 찾을 수 없습니다. userId=${command.userId}"
        )

        val group = groupOutPort.save(
            Group.create(
                name = command.name,
                maxMemberCount = command.maxMemberCount,
                ownerId = user.id!!,
            )
        )

        groupMemberOutPort.save(GroupMember.create(groupId = group.id!!, userId = user.id))

        return CreateGroupResult(group.id)
    }

    override fun getMyGroups(query: GetMyGroupsQuery): List<GetMyGroupsResult> {
        val groupMembers = groupMemberOutPort.findAllByUserId(query.userId)
        return groupMembers.map { member ->
            val group = groupOutPort.findById(member.groupId) ?:
                throw DomainException(ErrorCode.GROUP_NOT_FOUND, "그룹을 찾을 수 없습니다. groupId=${member.groupId}")

            val memberCount = groupMemberOutPort.countByGroupId(group.id!!)
            GetMyGroupsResult(
                groupId = group.id,
                name = group.name,
                maxMemberCount = group.maxMemberCount,
                memberCount = memberCount,
            )
        }
    }

    override fun joinGroup(command: JoinGroupCommand) {
        val group = groupOutPort.findById(command.groupId) ?:
            throw DomainException(ErrorCode.GROUP_NOT_FOUND, "그룹을 찾을 수 없습니다. groupId=${command.groupId}")

        val user = userOutPort.findById(command.userId) ?:
            throw DomainException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다. userId=${command.userId}")

        if (groupMemberOutPort.existsByGroupIdAndUserId(group.id!!, user.id!!)) {
            throw DomainException(ErrorCode.GROUP_ALREADY_JOINED, "이미 그룹에 참여한 사용자입니다. groupId=${group.id}, userId=${user.id}")
        }

        val currentCount = groupMemberOutPort.countByGroupId(group.id)
        if (currentCount >= group.maxMemberCount) { // TODO: 동시성 문제 해결 필요
            throw DomainException(ErrorCode.GROUP_FULL, "그룹의 최대 인원 수를 초과하였습니다. groupId=${group.id}, maxMemberCount=${group.maxMemberCount}")
        }

        groupMemberOutPort.save(GroupMember.create(groupId = group.id, userId = user.id))

        eventPublisher.publishEvent(
            GroupMemberJoinedEvent(
                groupId = group.id,
                joinedUserId = user.id,
            )
        )
    }

    override fun leaveGroup(command: LeaveGroupCommand) {
        val group = groupOutPort.findById(command.groupId) ?:
            throw DomainException(ErrorCode.GROUP_NOT_FOUND, "그룹을 찾을 수 없습니다. groupId=${command.groupId}")

        val user = userOutPort.findById(command.userId) ?:
            throw DomainException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다. userId=${command.userId}")

        groupMemberOutPort.findByGroupIdAndUserId(command.groupId, command.userId) ?:
            throw DomainException(ErrorCode.GROUP_MEMBER_NOT_FOUND, "그룹 멤버를 찾을 수 없습니다. groupId=${command.groupId}, userId=${command.userId}")

        groupMemberOutPort.deleteByGroupIdAndUserId(command.groupId, command.userId)

        eventPublisher.publishEvent(
            GroupMemberLeftEvent(
                groupId = group.id!!,
                leftMemberId = user.id!!,
            )
        )
    }

    override fun deleteGroup(command: DeleteGroupCommand) {
        val group = groupOutPort.findById(command.groupId) ?:
            throw DomainException(ErrorCode.GROUP_NOT_FOUND, "그룹을 찾을 수 없습니다. groupId=${command.groupId}")

        if (!group.isOwner(command.userId)) {
            throw DomainException(ErrorCode.NO_GROUP_DELETE_PERMISSION, "그룹 생성자만 그룹을 삭제할 수 있습니다. groupId=${group.id}, userId=${command.userId}")
        }

        groupMemberOutPort.deleteAllByGroupId(command.groupId)
        groupOutPort.deleteById(command.groupId)
    }
}