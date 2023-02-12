package com.lendsumapp.lendsum.data.model

data class Response<T>(
    val status: Status? = null,
    val data: T? = null,
    val error: Error? = null
)

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}

enum class Error{
    NO_INTERNET,
    INVALID_LOGIN,
    INVALID_EMAIL,
    FAILED_TO_SEND
}
