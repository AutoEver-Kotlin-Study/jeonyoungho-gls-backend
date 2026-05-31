package com.jeonyongho.gls.api.client

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SmsAdapter(
    private val smsHttpClient: SmsHttpClient,
) : SmsOutPort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(command: SmsSendCommand) {
        log.info("[SMS] Send request. from={}, to={}, content={}", command.from, command.to, command.content)

        try {
            smsHttpClient.send(
                SmsSendRq(
                    from = command.from,
                    to = command.to,
                    content = command.content,
                )
            )
            log.info("[SMS] Send success. from={}, to={}, content={}", command.from, command.to, command.content)
        } catch (e: Exception) {
            log.error("[SMS] Send failed. from={}, to={}, content={}", command.from, command.to, command.content)
            throw SmsSendException(command.from, command.to, "[SMS] Failed to send SMS to $command.to", e)
        }
    }

}