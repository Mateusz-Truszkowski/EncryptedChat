package com.example.messengerapp

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/users")
    suspend fun createUser(@Body user: User): User

    @POST("/login")
    suspend fun loginUser(@Body user: UserLogin): String

    @POST("/messages")
    suspend fun sendMessage(@Body msg: Message): Message

    @GET("/messages")
    suspend fun getMessages(): List<MessageDTO>

    @GET("/users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Int): User
}