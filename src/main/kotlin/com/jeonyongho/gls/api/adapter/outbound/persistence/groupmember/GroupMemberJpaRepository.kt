package com.jeonyongho.gls.api.adapter.outbound.persistence.groupmember

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface GroupMemberJpaRepository : JpaRepository<GroupMemberJpaEntity, Long> {
    fun findByGroupIdAndUserId(groupId: Long, userId: Long): GroupMemberJpaEntity?
    fun findAllByGroupId(groupId: Long): List<GroupMemberJpaEntity>
    fun findAllByUserId(userId: Long): List<GroupMemberJpaEntity>
    fun existsByGroupIdAndUserId(groupId: Long, userId: Long): Boolean
    fun countByGroupId(groupId: Long): Long

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM GroupMemberJpaEntity gm WHERE gm.groupId = :groupId AND gm.userId = :userId")
    fun deleteByGroupIdAndUserId(groupId: Long, userId: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM GroupMemberJpaEntity gm WHERE gm.groupId = :groupId")
    fun deleteAllByGroupId(groupId: Long)
}