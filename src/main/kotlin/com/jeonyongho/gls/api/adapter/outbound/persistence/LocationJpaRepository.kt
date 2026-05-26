package com.jeonyongho.gls.api.adapter.outbound.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface LocationJpaRepository : JpaRepository<LocationJpaEntity, Long>
