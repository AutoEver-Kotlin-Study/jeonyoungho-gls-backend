package com.jeonyongho.gls.api.adapter.outbound.persistence.location

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LocationJpaRepository : JpaRepository<LocationJpaEntity, Long> {
    fun findAllByUserIdOrderByRecordedAtDesc(userId: Long): List<LocationJpaEntity>

    @Query("""
        SELECT l FROM LocationJpaEntity l
        JOIN GroupMemberJpaEntity gm ON gm.userId = l.userId AND gm.groupId = :groupId
        WHERE l.recordedAt >= gm.joinedAt
        ORDER BY l.userId ASC, l.recordedAt DESC
    """)
    fun findLocationsByGroupId(groupId: Long): List<LocationJpaEntity>
}
