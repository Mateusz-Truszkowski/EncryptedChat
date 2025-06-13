package com.example.messengerapp.data.api

import com.example.messengerapp.data.model.Group
import com.example.messengerapp.data.model.Message
import com.example.messengerapp.data.model.Page
import com.example.messengerapp.data.model.TokenResponse
import com.example.messengerapp.data.model.User
import com.example.messengerapp.data.model.UserLogin
import okhttp3.ResponseBody
import retrofit2.Response
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

    @GET("users")
    suspend fun getAllUsers(
        @Header("Authorization") authToken: String
    ): List<User>

    @GET("/groups")
    suspend fun getGroups(
        @Header("Authorization") authToken: String
    ): List<Group>

    @POST("groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
        @Body group: Group
    ): Group

    @POST("groups/{groupId}/add_user")
    suspend fun addUserToGroup(
        @Path("groupId") groupId: Int,
        @Body user: User,
        @Header("Authorization") token: String
    ): Response<ResponseBody>
}