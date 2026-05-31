package com.jeonyongho.gls.api.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(e: DomainException): ResponseEntity<ApiResponse.Failure> {
        return ResponseEntity.badRequest()
            .body(
                ApiResponse.failure(
                    status = HttpStatus.BAD_REQUEST.value(),
                    code = e.errorCode.code,
                    message = e.errorCode.message,
                    detail = e.detail,
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse.Failure> {
        val errors = e.bindingResult.fieldErrors
            .joinToString(", ") { it.defaultMessage ?: "유효하지 않은 요청입니다." }
        return ResponseEntity.badRequest()
            .body(
                ApiResponse.failure(
                    status = HttpStatus.BAD_REQUEST.value(),
                    code = "common.invalid_request",
                    message = errors,
                )
            )
    }

    @ExceptionHandler(InvalidArgumentException::class)
    fun handleInvalidArgumentException(e: InvalidArgumentException): ResponseEntity<ApiResponse.Failure> {
        return ResponseEntity.badRequest()
            .body(
                ApiResponse.failure(
                    status = HttpStatus.BAD_REQUEST.value(),
                    code = "common.invalid_request",
                    message = e.message ?: "유효하지 않은 요청입니다.",
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse.Failure> {
        log.error("[EXCEPTION] Unexpected error", e)
        return ResponseEntity.internalServerError()
            .body(ApiResponse.failure(500, "common.internal_server_error", "서버 내부 오류가 발생했습니다."))
    }
}