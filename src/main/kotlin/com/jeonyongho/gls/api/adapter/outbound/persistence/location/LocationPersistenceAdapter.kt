package com.jeonyongho.gls.api.adapter.outbound.persistence.location

import com.jeonyongho.gls.api.application.port.outbound.LocationOutPort
import com.jeonyongho.gls.api.domain.Location
import org.springframework.stereotype.Repository

@Repository
class LocationPersistenceAdapter(
    private val locationJpaRepository: LocationJpaRepository
) : LocationOutPort {

    override fun saveAll(locations: List<Location>) {
        locationJpaRepository.saveAll(
            locations.map { LocationJpaEntity.from(it) }
        )
    }

    override fun findLatestLocationsByGroupId(groupId: Long): List<Location> {
        val entities = locationJpaRepository.findLocationsByGroupId(groupId);
        return entities.map { it.toDomain() }
    }
}