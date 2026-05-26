package com.jeonyongho.gls

import com.jeonyongho.gls.api.adapter.outbound.persistence.user.UserJpaEntity
import com.jeonyongho.gls.api.adapter.outbound.persistence.user.UserJpaRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class UserDataInitializer(
    private val userJpaRepository: UserJpaRepository,
) {
    @PostConstruct
    fun init() {
        userJpaRepository.deleteAll()

        val users = (0..9).map {
            UserJpaEntity(
                name = pickName(it),
                phoneNumber = generateRandomPhoneNumber(),
            )
        }

        userJpaRepository.saveAll(users)

        println("Dummy users created. count=${users.size}")
    }

    private fun pickName(index: Int): String {
        val names = listOf(
            "alice", "bob", "charlie", "david", "emma",
            "frank", "grace", "henry", "isabella", "jack"
        )

        return names[index]
    }

    private fun generateRandomPhoneNumber(): String {
        val middle = Random.nextInt(1000, 9999)
        val last = Random.nextInt(1000, 9999)

        return "010-$middle-$last"
    }
}
