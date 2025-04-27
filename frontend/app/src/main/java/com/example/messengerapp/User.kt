package com.example.messengerapp

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val email: String
)

data class UserLogin(
    val username: String,
    val password: String
)