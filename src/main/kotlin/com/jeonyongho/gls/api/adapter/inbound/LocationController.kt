package com.jeonyongho.gls.api.adapter.inbound

import com.jeonyongho.gls.api.adapter.inbound.rqrs.GetGroupLocationsRs
import com.jeonyongho.gls.api.adapter.inbound.rqrs.UpdateLocationRq
import com.jeonyongho.gls.api.application.port.inbound.GetGroupLocationsQuery
import com.jeonyongho.gls.api.application.port.inbound.GetGroupLocationsUseCase
import com.jeonyongho.gls.api.application.port.inbound.UpdateLocationCommand
import com.jeonyongho.gls.api.application.port.inbound.UpdateLocationUseCase
import com.jeonyongho.gls.api.exceptions.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class LocationController(
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val getGroupLocationsUseCase: GetGroupLocationsUseCase,
) {

    @PutMapping("/{groupId}/location")
    fun updateLocation(
        @RequestHeader("X-User-Id") userId: Long,
        @PathVariable groupId: Long,
        @Valid @RequestBody rq: UpdateLocationRq,
    ): ApiResponse.Success<Unit> {
        updateLocationUseCase.updateLocation(
            UpdateLocationCommand(
                groupId = groupId,
                userId = userId,
                locations = rq.locations.map {
                    UpdateLocationCommand.LocationEntry(
                        it.latitude,
                        it.longitude
                    )
                },
            )
        )
        return ApiResponse.success(Unit)
    }

    @GetMapping("/{groupId}/locations")
    fun getGroupLocations(
        @RequestHeader("X-User-Id") userId: Long,
        @PathVariable groupId: Long,
    ): ApiResponse.Success<List<GetGroupLocationsRs>> {
        val result = getGroupLocationsUseCase.getGroupLocations(
            GetGroupLocationsQuery(groupId = groupId, requesterId = userId)
        )
        return ApiResponse.success(result.map { GetGroupLocationsRs.from(it) })
    }
}