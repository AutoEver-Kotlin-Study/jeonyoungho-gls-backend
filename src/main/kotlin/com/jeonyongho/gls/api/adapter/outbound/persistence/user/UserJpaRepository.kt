package com.jeonyongho.gls.api.adapter.outbound.persistence.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long>