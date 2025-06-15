package com.example.messengerapp.data.api

import com.example.messengerapp.data.model.Group
import com.example.messengerapp.data.model.Message
import com.example.messengerapp.data.model.Page
import com.example.messengerapp.data.model.TokenResponse
import com.example.messengerapp.data.model.User
import com.example.messengerapp.data.model.UserLogin
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming

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
        @Path("groupId") groupId: Int,
        @Query("size") pageSize: Int = 100
    ): Page<Message>

    @GET("users")
    suspend fun getAllUsers(
        @Header("Authorization") authToken: String
    ): List<User>

    @DELETE("users/{username}")
    suspend fun deleteUser(
        @Path("username") username: String,
        @Header("Authorization") authToken: String
    ): String

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String,
        @Header("Authorization") authToken: String
    ): User

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

    @Multipart
    @POST("attachments")
    suspend fun uploadAttachment(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Map<String, String>

    @GET("attachments/{filename}")
    @Streaming
    suspend fun downloadAttachment(
        @Header("Authorization") token: String,
        @Path("filename") filename: String
    ): Response<ResponseBody>
}