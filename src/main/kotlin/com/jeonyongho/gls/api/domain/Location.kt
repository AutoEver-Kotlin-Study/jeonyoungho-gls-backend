package com.jeonyongho.gls.api.domain

import java.time.LocalDateTime

class Location(
    val id: Long?,
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: LocalDateTime,
) {
    companion object {
        fun create(userId: Long, latitude: Double, longitude: Double): Location = Location(
            id = 0,
            userId = userId,
            latitude = latitude,
            longitude = longitude,
            recordedAt = LocalDateTime.now(),
        )
    }
}