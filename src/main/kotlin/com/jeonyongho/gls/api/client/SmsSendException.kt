package com.jeonyongho.gls.api.client

class SmsSendException(
    val from: String,
    val to: String,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)