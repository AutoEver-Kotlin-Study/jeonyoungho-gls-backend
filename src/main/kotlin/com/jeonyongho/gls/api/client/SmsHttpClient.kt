package com.jeonyongho.gls.api.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.Duration

@Component
class SmsHttpClient(
    @Value("\${sms.base-url}") baseUrl: String,
) {
    private val restClient = RestClient.builder()
        .baseUrl(baseUrl)
        .requestFactory(clientHttpRequestFactory())
        .build()

    private fun clientHttpRequestFactory(): ClientHttpRequestFactory {
        val factory = SimpleClientHttpRequestFactory()
        factory.setConnectTimeout(Duration.ofMillis(1500))
        factory.setReadTimeout(Duration.ofMillis(2000))
        return factory
    }

    fun send(request: SmsSendRq) {
        restClient.post()
            .uri("/sms/send")
            .body(request)
            .retrieve()
            .toBodilessEntity()
    }
}