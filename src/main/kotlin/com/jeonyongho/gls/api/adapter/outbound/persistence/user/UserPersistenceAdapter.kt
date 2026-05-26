package com.jeonyongho.gls.api.adapter.outbound.persistence.user

import com.jeonyongho.gls.api.application.port.outbound.UserOutPort
import com.jeonyongho.gls.api.domain.User
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserOutPort {

    override fun findById(userId: Long): User? =
        userJpaRepository.findById(userId).orElse(null)?.toDomain()

    override fun save(user: User): User =
        userJpaRepository.save(UserJpaEntity.from(user)).toDomain()
}