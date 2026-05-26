package com.jeonyongho.gls.api.domain

class Group(
    val id: Long?,
    val name: String,
    val maxMemberCount: Int,
    val ownerId: Long
) {

    fun isOwner(userId: Long): Boolean = ownerId == userId

    companion object {
        fun create(name: String, maxMemberCount: Int, ownerId: Long): Group = Group(
            id = 0,
            name = name,
            maxMemberCount = maxMemberCount,
            ownerId = ownerId,
        )
    }
}
