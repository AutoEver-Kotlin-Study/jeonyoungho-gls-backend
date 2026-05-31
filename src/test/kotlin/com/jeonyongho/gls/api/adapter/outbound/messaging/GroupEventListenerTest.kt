package com.jeonyongho.gls.api.adapter.outbound.messaging

import com.jeonyongho.gls.api.adapter.outbound.eventlistener.GroupEventListener
import com.jeonyongho.gls.api.application.port.outbound.GroupMemberOutPort
import com.jeonyongho.gls.api.application.port.outbound.GroupOutPort
import com.jeonyongho.gls.api.application.port.outbound.UserOutPort
import com.jeonyongho.gls.api.client.SmsOutPort
import com.jeonyongho.gls.api.client.SmsSendCommand
import com.jeonyongho.gls.api.domain.Group
import com.jeonyongho.gls.api.domain.GroupMember
import com.jeonyongho.gls.api.domain.User
import com.jeonyongho.gls.api.domain.event.GroupMemberJoinedEvent
import com.jeonyongho.gls.api.domain.event.GroupMemberLeftEvent
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GroupEventSmsAdapter 테스트")
class GroupEventListenerTest {

    private lateinit var userOutPort: UserOutPort
    private lateinit var smsOutPort: SmsOutPort
    private lateinit var groupMemberOutPort: GroupMemberOutPort
    private lateinit var groupOutPort: GroupOutPort
    private lateinit var groupEventListener: GroupEventListener

    @BeforeEach
    fun setup() {
        userOutPort = mockk()
        smsOutPort = mockk()
        groupMemberOutPort = mockk()
        groupOutPort = mockk()

        groupEventListener = GroupEventListener(
            serviceNumber = "SENDER_NUMBER",
            userOutPort = userOutPort,
            smsOutPort = smsOutPort,
            groupMemberOutPort = groupMemberOutPort,
            groupOutPort = groupOutPort
        )
    }

    @Test
    @DisplayName("멤버 참여 이벤트 처리 - SMS 성공")
    fun testHandleGroupMemberJoined_Success() {
        // given
        val event = GroupMemberJoinedEvent(
            groupId = 1L,
            joinedUserId = 1L,
        )

        val joinedUser = User(id = 2L, name = "alice", phoneNumber = "010-1111-1111")
        val user3 = User(id = 3L, name = "bob", phoneNumber = "010-2222-2222")
        val user4 = User(id = 4L, name = "trudy", phoneNumber = "010-3333-3333")

        val group = mockk<Group>()
        every { group.name } returns "위치 공유 그룹1"
        every { groupOutPort.findById(event.groupId) } returns group

        val gm3 = mockk<GroupMember>()
        every { gm3.userId } returns 3L
        val gm4 = mockk<GroupMember>()
        every { gm4.userId } returns 4L
        every { groupMemberOutPort.findAllByGroupId(event.groupId) } returns listOf(gm3, gm4)

        every { userOutPort.findById(event.joinedUserId) } returns joinedUser
        every { userOutPort.findAllById(any()) } returns listOf(user3, user4)

        justRun { smsOutPort.send(any()) }

        // when
        groupEventListener.handleGroupMemberJoined(event)

        // then
        verify(exactly = 2) { smsOutPort.send(any()) }
    }

    @Test
    @DisplayName("멤버 참여 이벤트 처리 - 사용자 없을 때 (정상 처리)")
    fun testHandleGroupMemberJoined_UserNotFound() {
        // Given
        val event = GroupMemberJoinedEvent(
            groupId = 1L,
            joinedUserId = 2L,
        )

        val joinedUser = User(id = 2L, name = "alice", phoneNumber = "010-1111-1111")
        val user3 = User(id = 3L, name = "trudy", phoneNumber = "010-2222-2222")

        val group = mockk<Group>()
        every { group.name } returns "위치 공유 그룹1"
        every { groupOutPort.findById(event.groupId) } returns group

        val gm3 = mockk<GroupMember>()
        every { gm3.userId } returns 3L
        val gm4 = mockk<GroupMember>()
        every { gm4.userId } returns 4L
        every { groupMemberOutPort.findAllByGroupId(event.groupId) } returns listOf(gm3, gm4)

        every { userOutPort.findById(event.joinedUserId) } returns joinedUser
        every { userOutPort.findAllById(any()) } returns listOf(user3)

        justRun { smsOutPort.send(any()) }

        // ㅈhen
        groupEventListener.handleGroupMemberJoined(event)

        // then
        verify(exactly = 1) { smsOutPort.send(any()) }
    }

    @Test
    @DisplayName("멤버 퇴장 이벤트 처리 - SMS 성공")
    fun testHandleGroupMemberLeft_Success() {
        // Given
        val event = GroupMemberLeftEvent(
            groupId = 1L,
            leftMemberId = 2L,
        )

        val leftUser = User(id = 2L, name = "alice", phoneNumber = "010-1111-1111")
        val user3 = User(id = 3L, name = "bob", phoneNumber = "010-2222-2222")
        val user4 = User(id = 4L, name = "trudy", phoneNumber = "010-3333-3333")

        val group = mockk<Group>()
        every { group.name } returns "서울 러닝 클럽"
        every { groupOutPort.findById(event.groupId) } returns group

        val gm3 = mockk<GroupMember>()
        every { gm3.userId } returns 3L
        val gm4 = mockk<GroupMember>()
        every { gm4.userId } returns 4L
        every { groupMemberOutPort.findAllByGroupId(event.groupId) } returns listOf(gm3, gm4)

        every { userOutPort.findById(event.leftMemberId) } returns leftUser
        every { userOutPort.findAllById(any()) } returns listOf(user3, user4)

        justRun { smsOutPort.send(any()) }

        // When
        groupEventListener.handleGroupMemberLeft(event)

        // Then
        verify(exactly = 2) { smsOutPort.send(any()) }
    }

    @Test
    @DisplayName("멤버 퇴장 이벤트 처리 - 남은 멤버 없을 때")
    fun testHandleGroupMemberLeft_NoRemainingMembers() {
        // Given
        val event = GroupMemberLeftEvent(
            groupId = 1L,
            leftMemberId = 2L,
        )

        val leftUser = User(id = 2L, name = "alice", phoneNumber = "010-1111-1111")

        val group = mockk<Group>()
        every { group.name } returns "위치 공유 그룹1"
        every { groupOutPort.findById(event.groupId) } returns group

        every { groupMemberOutPort.findAllByGroupId(event.groupId) } returns emptyList()
        every { userOutPort.findById(event.leftMemberId) } returns leftUser
        every { userOutPort.findAllById(any()) } returns emptyList()

        // when
        groupEventListener.handleGroupMemberLeft(event)

        // then
        verify(exactly = 0) { smsOutPort.send(any()) }
    }

    @Test
    @DisplayName("SMS 발송 실패 시에도 다른 멤버 발송 계속")
    fun testHandleGroupMemberJoined_PartialSmsFailure() {
        // Given
        val event = GroupMemberJoinedEvent(
            groupId = 1L,
            joinedUserId = 2L,
        )

        val joinedUser = User(id = 2L, name = "alice", phoneNumber = "010-1111-1111")
        val user3 = User(id = 3L, name = "bob", phoneNumber = "010-2222-2222")
        val user4 = User(id = 4L, name = "trudy", phoneNumber = "010-3333-3333")
        val user5 = User(id = 5L, name = "tom", phoneNumber = "010-4444-4444")

        val group = mockk<Group>()
        every { group.name } returns "서울 러닝 클럽"
        every { groupOutPort.findById(event.groupId) } returns group

        val gm3 = mockk<GroupMember>()
        every { gm3.userId } returns 3L
        val gm4 = mockk<GroupMember>()
        every { gm4.userId } returns 4L
        val gm5 = mockk<GroupMember>()
        every { gm5.userId } returns 5L
        every { groupMemberOutPort.findAllByGroupId(event.groupId) } returns listOf(gm3, gm4, gm5)

        every { userOutPort.findById(event.joinedUserId) } returns joinedUser
        every { userOutPort.findAllById(any()) } returns listOf(user3, user4, user5)

        val smsException = RuntimeException("SMS 서버 에러")
        val capturedCommands = mutableListOf<SmsSendCommand>()
        every { smsOutPort.send(any()) } answers { call ->
            val command = call.invocation.args[0] as SmsSendCommand
            capturedCommands.add(command)
            if (command.to == "010-2222-2222") {
                throw smsException
            }
        }

        // when
        groupEventListener.handleGroupMemberJoined(event)

        // then
        verify(exactly = 3) { smsOutPort.send(any()) }
    }
}
