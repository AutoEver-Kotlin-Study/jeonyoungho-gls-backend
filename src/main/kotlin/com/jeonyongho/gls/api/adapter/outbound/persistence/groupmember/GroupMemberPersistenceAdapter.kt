package com.jeonyongho.gls.api.adapter.outbound.persistence.groupmember

import com.jeonyongho.gls.api.application.port.outbound.GroupMemberOutPort
import com.jeonyongho.gls.api.domain.GroupMember
import org.springframework.stereotype.Repository

@Repository
class GroupMemberRepositoryAdapter(
    private val groupMemberJpaRepository: GroupMemberJpaRepository,
) : GroupMemberOutPort {

    override fun save(groupMember: GroupMember): GroupMember =
        groupMemberJpaRepository.save(GroupMemberJpaEntity.from(groupMember)).toDomain()

    override fun findByGroupIdAndUserId(groupId: Long, userId: Long): GroupMember? =
        groupMemberJpaRepository.findByGroupIdAndUserId(groupId, userId)?.toDomain()

    override fun findAllByGroupId(groupId: Long): List<GroupMember> =
        groupMemberJpaRepository.findAllByGroupId(groupId).map { it.toDomain() }

    override fun findAllByUserId(userId: Long): List<GroupMember> =
        groupMemberJpaRepository.findAllByUserId(userId).map { it.toDomain() }

    override fun existsByGroupIdAndUserId(groupId: Long, userId: Long): Boolean =
        groupMemberJpaRepository.existsByGroupIdAndUserId(groupId, userId)

    override fun countByGroupId(groupId: Long): Long =
        groupMemberJpaRepository.countByGroupId(groupId)

    override fun deleteByGroupIdAndUserId(groupId: Long, userId: Long) =
        groupMemberJpaRepository.deleteByGroupIdAndUserId(groupId, userId)

    override fun deleteAllByGroupId(groupId: Long) =
        groupMemberJpaRepository.deleteAllByGroupId(groupId)
}