package com.jeonyongho.gls.api.adapter.outbound.persistence

import com.jeonyongho.gls.api.domain.Location
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "locations",
    indexes = [Index(name = "idx_location_user_recorded", columnList = "user_id, recorded_at DESC")],
)
class LocationJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Long,

    val latitude: Double,
    val longitude: Double,
    val recordedAt: LocalDateTime,
) : BaseTimeEntity() {
    fun toDomain(): Location = Location(
        id = id,
        userId = userId,
        latitude = latitude,
        longitude = longitude,
        recordedAt = recordedAt,
    )

    companion object {
        fun from(location: Location): LocationJpaEntity = LocationJpaEntity(
            id = location.id,
            userId = location.userId,
            latitude = location.latitude,
            longitude = location.longitude,
            recordedAt = location.recordedAt,
        )
    }
}