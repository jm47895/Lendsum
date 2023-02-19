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
    PASS_NO_MATCH,
    FAILED_TO_CREATE_USER,
    USER_EMAIL_ALREADY_EXISTS,
    FAILED_TO_LINK_FIREBASE,
    LINK_ALREADY_EXISTS,
    FAILED_TO_GET_GOOGLE_INFO,
    USER_NOT_FOUND
}
