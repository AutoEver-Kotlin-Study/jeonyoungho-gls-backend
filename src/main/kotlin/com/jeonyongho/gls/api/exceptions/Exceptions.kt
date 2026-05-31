package com.jeonyongho.gls.api.exceptions

class DomainException : RuntimeException {
    val errorCode: ErrorCode
    val detail: String?

    constructor(errorCode: ErrorCode) : super(errorCode.message) {
        this.errorCode = errorCode
        this.detail = null
    }

    constructor(errorCode: ErrorCode, detail: String) : super(errorCode.message) {
        this.errorCode = errorCode
        this.detail = detail
    }

    constructor(errorCode: ErrorCode, cause: Throwable) : super(errorCode.message, cause) {
        this.errorCode = errorCode
        this.detail = null
    }
}

class InvalidArgumentException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}