package com.jeonyongho.gls.api.client

interface SmsPort {
    fun send(request: SmsSendCommand)
}

data class SmsSendCommand(
    val to: String,
    val content: String,
)
