package com.jeonyongho.gls.api.adapter.outbound.persistence.group


import com.jeonyongho.gls.api.application.port.outbound.GroupOutPort
import com.jeonyongho.gls.api.domain.Group
import org.springframework.stereotype.Repository

@Repository
class GroupRepositoryAdapter(
    private val groupJpaRepository: GroupJpaRepository,
) : GroupOutPort {

    override fun save(group: Group): Group =
        groupJpaRepository.save(GroupJpaEntity.from(group)).toDomain()

    override fun findById(groupId: Long): Group? =
        groupJpaRepository.findById(groupId).orElse(null)?.toDomain()

    override fun deleteById(groupId: Long) =
        groupJpaRepository.deleteById(groupId)
}