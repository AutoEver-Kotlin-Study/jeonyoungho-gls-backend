package com.jeonyongho.gls.api.domain

class User(
    val id: Long?,
    val name: String,
    val phoneNumber: String
) {
    companion object {
        fun create(name: String, phoneNumber: String): User = User(
            id = null,
            name = name,
            phoneNumber = phoneNumber,
        )
    }
}