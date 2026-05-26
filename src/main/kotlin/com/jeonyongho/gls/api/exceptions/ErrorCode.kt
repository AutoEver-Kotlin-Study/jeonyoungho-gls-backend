package com.jeonyongho.gls.api.exceptions

enum class ErrorCode(
    val code: String,
    val message: String,
) {
    // group
    GROUP_NOT_FOUND("group.not_found", "그룹을 찾을 수 없습니다."),
    GROUP_FULL("group.full", "그룹 최대 인원을 초과했습니다."),
    GROUP_ALREADY_JOINED("group.already_joined", "이미 참여한 그룹입니다."),
    GROUP_MEMBER_NOT_FOUND("group.member_not_found", "그룹 멤버를 찾을 수 없습니다."),
    NO_GROUP_DELETE_PERMISSION("group.no_group_delete_permission", "그룹 생성자만 삭제할 수 있습니다."),

    // user
    USER_NOT_FOUND("user.not_found", "사용자를 찾을 수 없습니다."),
}
