package com.jeonyongho.gls.api.adapter.outbound.eventlistener

import com.jeonyongho.gls.api.application.port.outbound.GroupMemberOutPort
import com.jeonyongho.gls.api.application.port.outbound.GroupOutPort
import com.jeonyongho.gls.api.application.port.outbound.UserOutPort
import com.jeonyongho.gls.api.client.SmsOutPort
import com.jeonyongho.gls.api.client.SmsSendCommand
import com.jeonyongho.gls.api.domain.User
import com.jeonyongho.gls.api.domain.event.GroupMemberJoinedEvent
import com.jeonyongho.gls.api.domain.event.GroupMemberLeftEvent
import com.jeonyongho.gls.api.exceptions.DomainException
import com.jeonyongho.gls.api.exceptions.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class GroupEventListener(
    @Value("\${sms.service-number}") private val serviceNumber: String,
    private val userOutPort: UserOutPort,
    private val smsOutPort: SmsOutPort,
    private val groupMemberOutPort: GroupMemberOutPort,
    private val groupOutPort: GroupOutPort,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Async("groupSmsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleGroupMemberJoined(event: GroupMemberJoinedEvent) {
        log.info(
            "[Event] GroupMemberJoined event processing start. groupId={}, joinedUserId={}",
            event.groupId,
            event.joinedUserId
        )

        val group = groupOutPort.findById(event.groupId) ?: throw DomainException(
            ErrorCode.USER_NOT_FOUND,
            "그룹을 찾을 수 없습니다. groupId=${event.groupId}"
        )

        val joinedUser = userOutPort.findById(event.joinedUserId) ?: throw DomainException(
            ErrorCode.USER_NOT_FOUND,
            "사용자를 찾을 수 없습니다. userId=${event.joinedUserId}"
        )

        val existingUsers = findGroupUsersExcluding(event.groupId, event.joinedUserId)
        val phoneNumbers = existingUsers.map { it.phoneNumber }.toSet()
        val message = "${joinedUser.name}님이 그룹 [${group.name}]에 참여하였습니다."

        phoneNumbers.forEach { phoneNumber ->
            smsOutPort.send(
                SmsSendCommand(
                    from = serviceNumber,
                    to = phoneNumber,
                    content = message
                )
            )
        }

        log.info(
            "[Event] GroupMemberJoined event processing completed. groupId={}, recipientCount={}",
            event.groupId,
            phoneNumbers.size
        )
    }

    @Async("groupSmsExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleGroupMemberLeft(event: GroupMemberLeftEvent) {
        log.info(
            "[Event] GroupMemberLeft event processing start. groupId={}, leftMemberId={}",
            event.groupId,
            event.leftMemberId
        )

        val group = groupOutPort.findById(event.groupId) ?: throw DomainException(
            ErrorCode.USER_NOT_FOUND,
            "그룹을 찾을 수 없습니다. groupId=${event.groupId}"
        )

        val leftUser = userOutPort.findById(event.leftMemberId) ?: throw DomainException(
            ErrorCode.USER_NOT_FOUND,
            "사용자를 찾을 수 없습니다. userId=${event.leftMemberId}"
        )

        val existingUsers = findGroupUsersExcluding(event.groupId, event.leftMemberId)
        val phoneNumbers = existingUsers.map { it.phoneNumber }.toSet()
        val message = "${leftUser.name}님이 그룹 [${group.name}]에서 퇴장하였습니다."

        phoneNumbers.forEach { phoneNumber ->
            smsOutPort.send(
                SmsSendCommand(
                    from = serviceNumber,
                    to = phoneNumber,
                    content = message
                )
            )
            log.debug(
                "[SMS] GroupMemberLeft SMS request sent. groupId={}, phoneNumber={}",
                event.groupId,
                phoneNumber
            )

        }

        log.info(
            "[Event] GroupMemberLeft event processing completed. groupId={}, recipientCount={}",
            event.groupId,
            phoneNumbers.size
        )
    }

    private fun findGroupUsersExcluding(groupId: Long, excludedUserId: Long): List<User> {
        val existingUserIds = groupMemberOutPort.findAllByGroupId(groupId)
            .filter { it.userId != excludedUserId }
            .map { it.userId }
            .toSet()

        return userOutPort.findAllById(existingUserIds)
    }
}
