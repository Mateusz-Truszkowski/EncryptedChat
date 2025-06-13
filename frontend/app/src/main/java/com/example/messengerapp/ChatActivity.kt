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
import com.example.messengerapp.data.api.RetrofitClient
import com.example.messengerapp.data.api.StompClient
import com.example.messengerapp.data.model.Group
import com.example.messengerapp.data.model.Message

class ChatActivity : AppCompatActivity() {
    private lateinit var sendBtn : Button
    private lateinit var backBtn : Button
    private lateinit var messageBody : String
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val groupId = intent.getIntExtra("group_id", -1)
        val groupName = intent.getStringExtra("group_name")
        val token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "")
        val bearerToken = "Bearer $token"

        recyclerView = findViewById<RecyclerView>(R.id.message_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = RetrofitClient.apiService.getMessages(bearerToken, groupId)

                withContext(Dispatchers.Main) {
                    // Ustawienie wiadomości w adapterze
                    val username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", null)
                    messageAdapter = MessageAdapter(messages.content, username)
                    recyclerView.adapter = messageAdapter
                }
            } catch (e: Exception) {
                Log.e("API ERROR", "Failed to fetch messages: ${e.message}")
            }
        }

        val client = StompClient(groupId, bearerToken.substring(7)) { payload ->
            Log.d("WS", "Odebrano wiadomość: $payload")
            runOnUiThread {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val messages = RetrofitClient.apiService.getMessages(bearerToken, groupId)
                        withContext(Dispatchers.Main) {
                            messageAdapter.updateMessages(messages.content)
                            recyclerView.scrollToPosition(messages.content.size - 1)
                        }
                    } catch (e: Exception) {
                        Log.e("API ERROR", "Failed to fetch messages: ${e.message}")
                    }
                }
            }
        }
        client.connect()

        val chatTitle = findViewById<TextView>(R.id.chat_title)

        chatTitle.text = groupName

        sendBtn = findViewById(R.id.send_button)

        sendBtn.setOnClickListener {
            messageBody = findViewById<EditText?>(R.id.message_input).text.toString()

            val group = Group(
                id = groupId,
                name = ""
            )
            val msg = Message(
                id = null,
                group = group,
                sender = null,
                content = messageBody,
                sent_at = null,
                attachment = null,
                status = null
            )

            findViewById<EditText>(R.id.message_input).text.clear()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val message = RetrofitClient.apiService.sendMessage(bearerToken, msg)
                    Log.i("API SUCCESS", "Sent message: $message")
                } catch (e: Exception) {
                    Log.e("API ERROR", "Failed to log in: ${e.message}")
                }
            }
        }

        backBtn = findViewById(R.id.back_button)

        backBtn.setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
        }
    }
}