package com.example.messengerapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent

class ChatActivity : AppCompatActivity() {
    private lateinit var sendBtn : Button
    private lateinit var backBtn : Button
    private lateinit var messageBody : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val recyclerView = findViewById<RecyclerView>(R.id.message_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = RetrofitClient.apiService.getMessages()
                withContext(Dispatchers.Main) {
                    // Ustawienie wiadomości w adapterze
                    val adapter = MessageAdapter(messages, 15)
                    recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                Log.e("API ERROR", "Failed to fetch messages: ${e.message}")
            }
        }

        val chatTitle = findViewById<TextView>(R.id.chat_title)

        val username = intent.getStringExtra("username") ?: getString(R.string.usernameChat)
        chatTitle.text = username

        val receiverId = intent.getIntExtra("receiverId", -1)

        if(receiverId == -1) {
            throw RuntimeException("What happened?")
        }

        sendBtn = findViewById(R.id.send_button)

        sendBtn.setOnClickListener {
            messageBody = findViewById<EditText?>(R.id.message_input).text.toString()

            val msg = Message (
                id = 69,
                sender_id = 15,
                receiver_id = receiverId,
                message = messageBody,
                key = "Test",
                status = "Sent"
            )

            findViewById<EditText>(R.id.message_input).text.clear()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val message = RetrofitClient.apiService.sendMessage(msg)
                    Log.i("API SUCCESS", "Sent message: $message")
                } catch (e: Exception) {
                    Log.e("API ERROR", "Failed to log in: ${e.message}")
                }
            }
            recreate()
        }

        backBtn = findViewById(R.id.back_button)

        backBtn.setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
        }
    }
}