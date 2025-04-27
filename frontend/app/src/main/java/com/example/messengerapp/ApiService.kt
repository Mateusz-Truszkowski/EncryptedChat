package com.example.messengerapp

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/users")
    suspend fun createUser(@Body user: User): User
}