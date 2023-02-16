package com.lendsumapp.lendsum.data.model

data class Response<T>(
    val status: Status? = null,
    val data: T? = null,
    val error: LendsumError? = null
)

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}

enum class LendsumError{
    NONE,
    NO_INTERNET,
    INVALID_LOGIN,
    INVALID_EMAIL,
    FAILED_TO_SEND,
    EMPTY_FIRST_NAME,
    EMPTY_LAST_NAME,
    INVALID_PASS,
    PASS_NO_MATCH
}
