package com.example.messengerapp.data.api

import com.example.messengerapp.data.model.Group
import com.example.messengerapp.data.model.Message
import com.example.messengerapp.data.model.Page
import com.example.messengerapp.data.model.TokenResponse
import com.example.messengerapp.data.model.User
import com.example.messengerapp.data.model.UserLogin
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Header

interface ApiService {
    @POST("/users")
    suspend fun createUser(@Body user: User): User

    @POST("/auth")
    suspend fun loginUser(@Body user: UserLogin): TokenResponse

    @POST("/messages")
    suspend fun sendMessage(
        @Header("Authorization") authToken: String,
        @Body msg: Message
    ): Message

    @GET("/messages/{groupId}")
    suspend fun getMessages(
        @Header("Authorization") authToken: String,
        @Path("groupId") groupId: Int
    ): Page<Message>

    @GET("/users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Int): User

    @GET("/groups")
    suspend fun getGroups(
        @Header("Authorization") authToken: String
    ): List<Group>
}