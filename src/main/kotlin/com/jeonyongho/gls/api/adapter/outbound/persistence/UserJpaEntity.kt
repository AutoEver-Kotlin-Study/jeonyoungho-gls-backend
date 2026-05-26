package com.jeonyongho.gls.api.adapter.outbound.persistence

import com.jeonyongho.gls.api.domain.User
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserJpaEntity(
    val name: String,
    val phoneNumber: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseTimeEntity() {

    fun toDomain(): User = User(
        id = id,
        name = name,
        phoneNumber = phoneNumber
    )

    companion object {
        fun from(user: User): UserJpaEntity = UserJpaEntity(
            id = user.id,
            name = user.name,
            phoneNumber = user.phoneNumber,
        )
    }
}