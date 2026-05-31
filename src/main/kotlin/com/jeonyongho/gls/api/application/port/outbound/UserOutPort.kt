package com.jeonyongho.gls.api.application.port.outbound

import com.jeonyongho.gls.api.domain.User

interface UserOutPort {
    fun findById(userId: Long): User?
    fun findAllById(userId: Collection<Long>): List<User>
    fun save(user: User): User
}