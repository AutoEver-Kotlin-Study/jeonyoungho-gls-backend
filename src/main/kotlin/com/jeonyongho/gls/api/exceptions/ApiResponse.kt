package com.jeonyongho.gls.api.exceptions

import org.springframework.http.HttpStatus

sealed class ApiResponse {

    data class Success<T>(
        val status: Int,
        val data: T,
    ) : ApiResponse()

    data class Failure(
        val status: Int,
        val code: String,
        val message: String,
        val detail: String? = null,
    ) : ApiResponse()

    companion object {
        fun <T> success(status: Int, data: T): Success<T> = Success(status, data)

        fun <T> success(data: T): Success<T> = Success(HttpStatus.OK.value(), data)

        fun failure(status: Int, code: String, message: String, detail: String? = null): Failure =
            Failure(status, code, message, detail)
    }
}