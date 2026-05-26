package com.jeonyongho.gls.api.application.port.inbound

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

// command
data class UpdateLocationCommand(
    val groupId: Long,
    val userId: Long,
    val locations: List<LocationEntry>,
) {
    data class LocationEntry(
        @field:NotNull(message = "위도는 필수입니다.")
        val latitude: Double,

        @field:NotNull(message = "경도는 필수입니다.")
        val longitude: Double,
    )
}

data class GetGroupLocationsQuery(
    val groupId: Long,
    val requesterId: Long
)

// result
data class GetGroupLocationsResult(
    val userId: Long,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: LocalDateTime,
)