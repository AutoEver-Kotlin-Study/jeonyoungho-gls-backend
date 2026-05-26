package com.jeonyongho.gls.api.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SmsAdapter(
    private val smsHttpClient: SmsHttpClient,
    @Value("\${sms.service-number}") private val serviceNumber: String,
) : SmsPort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(request: SmsSendCommand) {
        log.info("[SMS] Send request. to={}, content={}", request.to, request.content)

        runCatching {
            smsHttpClient.send(
                SmsSendRq(
                    from = serviceNumber,
                    to = request.to,
                    content = request.content,
                )
            )
        }.onSuccess {
            log.info("[SMS] Send success. to={}", request.to)
        }.onFailure { e ->
            log.error("[SMS] Send failed. to={}, content={}", request.to, request.content, e)
        }
    }
}