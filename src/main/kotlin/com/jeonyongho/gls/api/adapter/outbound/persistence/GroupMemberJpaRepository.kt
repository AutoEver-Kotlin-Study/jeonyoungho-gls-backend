package com.jeonyongho.gls.api.adapter.outbound.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface GroupMemberJpaRepository : JpaRepository<GroupMemberJpaEntity, Long>