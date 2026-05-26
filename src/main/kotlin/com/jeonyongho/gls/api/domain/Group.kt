package com.jeonyongho.gls.api.domain

class Group(
    val id: Long?,
    val name: String,
    val maxMemberCount: Int,
    val ownerId: Long
) {
    fun validateOwner(userId: Long) {
        require(isOwner(userId)) { "그룹 생성자만 삭제할 수 있습니다." }
    }

    private fun isOwner(userId: Long): Boolean = ownerId == userId

    companion object {
        fun create(name: String, maxMemberCount: Int, ownerId: Long): Group = Group(
            id = 0,
            name = name,
            maxMemberCount = maxMemberCount,
            ownerId = ownerId,
        )
    }
}
