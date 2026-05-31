package com.jeonyongho.gls.api.client

interface SmsOutPort {
    fun send(command: SmsSendCommand)
}

data class SmsSendCommand(
    val from: String,
    val to: String,
    val content: String,
)
