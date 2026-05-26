package com.jeonyongho.gls.api.application.port.inbound

interface UpdateLocationUseCase {
    fun updateLocation(command: UpdateLocationCommand)
}

interface GetGroupLocationsUseCase {
    fun getGroupLocations(query: GetGroupLocationsQuery): List<GetGroupLocationsResult>
}