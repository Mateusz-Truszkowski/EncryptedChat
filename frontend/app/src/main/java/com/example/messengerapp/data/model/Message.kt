package com.example.messengerapp.data.model

data class Message (
    val id: Int?,
    val group: Group,
    val sender: User?,
    val content: String,
    val sent_at: String?,
    val attachment: String?,
    val status: String?
)

data class MessageDTO(
    val id: Int,
    val sender_id: Int,
    val receiver_id: Int,
    val message: String,
    val key: String,
    val timestamp: String,
    val status: String
)
