package com.jeonyongho.gls.api.adapter.outbound.persistence.group

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface GroupJpaRepository : JpaRepository<GroupJpaEntity, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
            UPDATE GroupJpaEntity g 
            SET g.currentMemberCount = g.currentMemberCount + 1
            WHERE g.id = :groupId 
            AND g.currentMemberCount < g.maxMemberCount
        """
    )
    fun increaseCurrentMemberCount(groupId: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE GroupJpaEntity g
        SET g.currentMemberCount = g.currentMemberCount - 1
        WHERE g.id = :groupId
        AND g.currentMemberCount > 0
    """)
    fun decreaseCurrentMemberCount(groupId: Long): Int
}