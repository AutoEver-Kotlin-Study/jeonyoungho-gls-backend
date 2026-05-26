package com.jeonyongho.gls.api.application.port.outbound

import com.jeonyongho.gls.api.domain.Group

interface GroupOutPort {
    fun save(group: Group): Group
    fun findById(groupId: Long): Group?
    fun deleteById(groupId: Long)
}