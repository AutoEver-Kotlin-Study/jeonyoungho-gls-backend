package com.jeonyongho.gls.api.adapter.inbound.rqrs

import com.jeonyongho.gls.api.application.port.inbound.CreateGroupResult
import com.jeonyongho.gls.api.application.port.inbound.GetMyGroupsResult
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateGroupRq(
    @field:NotBlank(message = "그룹명은 필수입니다.")
    val name: String,

    @field:NotNull(message = "최대 참여 인원은 필수입니다.")
    @field:Positive(message = "최대 참여 인원은 1 이상이어야 합니다.")
    val maxMemberCount: Int,
)

data class CreateGroupRs(
    val id: Long,
) {
    companion object {
        fun from(result: CreateGroupResult) = CreateGroupRs(
            id = result.groupId
        )
    }
}

data class GetMyGroupsRs(
    val groupId: Long,
    val name: String,
    val maxMemberCount: Int,
    val memberCount: Long,
) {
    companion object {
        fun from(result: GetMyGroupsResult) = GetMyGroupsRs(
            groupId = result.groupId,
            name = result.name,
            maxMemberCount = result.maxMemberCount,
            memberCount = result.memberCount,
        )
    }
}