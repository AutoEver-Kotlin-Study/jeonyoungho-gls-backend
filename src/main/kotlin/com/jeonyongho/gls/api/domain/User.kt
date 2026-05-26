package com.jeonyongho.gls.api.domain

import java.time.LocalDateTime

class User(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun create(name: String, phoneNumber: String): User = User(
            id = 0,
            name = name,
            phoneNumber = phoneNumber,
            createdAt = LocalDateTime.now(),
        )
    }
}