package com.example.messengerapp.data.model

import java.time.LocalDateTime

data class User(
    val id: Int?,
    val username: String,
    val password: String,
        val role: String,
    val last_activity: LocalDateTime?
)

data class UserLogin(
    val username: String,
    val password: String
)

data class TokenResponse(
    val token: String
)