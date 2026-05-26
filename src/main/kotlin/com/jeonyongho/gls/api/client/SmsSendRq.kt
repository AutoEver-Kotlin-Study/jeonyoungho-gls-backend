package com.jeonyongho.gls.api.client

data class SmsSendRq(
    val from: String,
    val to: String,
    val content: String,
)