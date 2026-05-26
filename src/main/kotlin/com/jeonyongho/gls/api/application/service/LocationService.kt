package com.jeonyongho.gls.api.application.service

import com.jeonyongho.gls.api.application.port.inbound.*
import com.jeonyongho.gls.api.application.port.outbound.GroupMemberOutPort
import com.jeonyongho.gls.api.application.port.outbound.LocationOutPort
import com.jeonyongho.gls.api.application.port.outbound.UserOutPort
import com.jeonyongho.gls.api.domain.Location
import com.jeonyongho.gls.api.exceptions.DomainException
import com.jeonyongho.gls.api.exceptions.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LocationService(
    private val locationOutPort: LocationOutPort,
    private val groupMemberOutPort: GroupMemberOutPort,
    private val userOutPort: UserOutPort,
) : UpdateLocationUseCase, GetGroupLocationsUseCase {

    @Transactional
    override fun updateLocation(command: UpdateLocationCommand) {
        groupMemberOutPort.findByGroupIdAndUserId(command.groupId, command.userId) ?:
            throw DomainException(ErrorCode.GROUP_MEMBER_NOT_FOUND, "그룹 멤버를 찾을 수 없습니다. groupId=${command.groupId}, userId=${command.userId}")

        val locations = command.locations.map { entry ->
            Location.create(
                userId = command.userId,
                latitude = entry.latitude,
                longitude = entry.longitude,
            )
        }
        locationOutPort.saveAll(locations)
    }

    override fun getGroupLocations(query: GetGroupLocationsQuery): List<GetGroupLocationsResult> {
        groupMemberOutPort.findByGroupIdAndUserId(query.groupId, query.requesterId)
            ?: throw DomainException(ErrorCode.GROUP_MEMBER_NOT_FOUND, "groupId=${query.groupId}, userId=${query.requesterId}")

        return locationOutPort.findLatestLocationsByGroupId(query.groupId)
            .map { loc ->
                val user = userOutPort.findById(loc.userId)
                    ?: throw DomainException(ErrorCode.USER_NOT_FOUND, "userId=${loc.userId}")

                GetGroupLocationsResult(
                    userId = user.id!!,
                    userName = user.name,
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    recordedAt = loc.recordedAt,
                )
            }
    }
}