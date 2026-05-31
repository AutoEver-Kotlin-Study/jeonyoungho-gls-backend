package com.jeonyongho.gls.api.adapter.inbound.rqrs

import com.jeonyongho.gls.api.application.port.inbound.GetGroupLocationsResult
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

// Request
data class UpdateLocationRq(
    @field:NotEmpty(message = "위치 정보는 필수입니다.")
    val locations: List<LocationEntryRq>,
) {
    data class LocationEntryRq(
        @field:NotNull(message = "위도는 필수입니다.")
        val latitude: Double,

        @field:NotNull(message = "경도는 필수입니다.")
        val longitude: Double,
    )
}

// Response
data class GetGroupLocationsRs(
    val userId: Long,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: LocalDateTime,
) {
    companion object {
        fun from(result: GetGroupLocationsResult) = GetGroupLocationsRs(
            userId = result.userId,
            userName = result.userName,
            latitude = result.latitude,
            longitude = result.longitude,
            recordedAt = result.recordedAt,
        )
    }
}