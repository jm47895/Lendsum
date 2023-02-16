package com.lendsumapp.lendsum.data.model

data class AccountForm(
    val firstName: String,
    val lastName: String,
    val email: String,
    val pass: String,
    val matchPass: String
)
