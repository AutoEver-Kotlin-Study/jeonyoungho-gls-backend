package com.jeonyongho.gls.api.application.port.outbound

import com.jeonyongho.gls.api.domain.Location

interface LocationOutPort {
    fun saveAll(locations: List<Location>)
    fun findLatestLocationsByGroupId(groupId: Long): List<Location>
}