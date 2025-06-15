package com.example.messengerapp

import StompClient
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.data.api.RetrofitClient
import com.example.messengerapp.data.model.Group
import com.example.messengerapp.data.model.Message
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var sendBtn: Button
    private lateinit var backBtn: Button
    private lateinit var micBtn: ImageButton
    private lateinit var voiceBtn: ImageButton
    private lateinit var messageInput: EditText
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private lateinit var audioFile: File

    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val groupId = intent.getIntExtra("group_id", -1)
        val groupName = intent.getStringExtra("group_name")
        val token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", "") ?: ""
        val bearerToken = "Bearer $token"

        // Widoki
        messageInput = findViewById(R.id.message_input)
        sendBtn = findViewById(R.id.send_button)
        backBtn = findViewById(R.id.back_button)
        micBtn = findViewById(R.id.mic_button)
        voiceBtn = findViewById(R.id.voice_button)
        val chatTitle = findViewById<TextView>(R.id.chat_title)
        chatTitle.text = groupName

        recyclerView = findViewById(R.id.message_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendBtn.performClick() // lub wywołaj metodę wysyłania
                true
            } else {
                false
            }
        }

        // Pobieranie wiadomości
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = RetrofitClient.apiService.getMessages(bearerToken, groupId)
                withContext(Dispatchers.Main) {
                    val username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", null)
                    messageAdapter = MessageAdapter(messages.content, username)
                    recyclerView.adapter = messageAdapter
                }
            } catch (e: Exception) {
                Log.e("API ERROR", "Failed to fetch messages: ${e.message}")
            }
        }

        // STOMP WebSocket
        val client = StompClient(groupId, token) { payload ->
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
        client.connect(this)

        // Rejestracja activity result launchera
        speechRecognizerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val speechResults = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!speechResults.isNullOrEmpty()) {
                    messageInput.setText(speechResults[0])
                }
            }
        }

        // Przycisk: wyślij wiadomość
        sendBtn.setOnClickListener {
            val messageBody = messageInput.text.toString()
            val group = Group(id = groupId, name = "")
            val msg = Message(null, group, null, messageBody, null, null, null)
            messageInput.text.clear()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.apiService.sendMessage(bearerToken, msg)
                } catch (e: Exception) {
                    Log.e("API ERROR", "Failed to send message: ${e.message}")
                }
            }
        }

        // Przycisk: wróć
        backBtn.setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        // Przycisk: rozpoznawanie mowy
        micBtn.setOnClickListener {
            startVoiceInput()
        }

        // Przycisk: nagrywanie głosówki
        voiceBtn.setOnClickListener {
            if (!isRecording) {
                if (checkMicPermission()) {
                    startRecording()
                } else {
                    requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 200)
                }
            } else {
                stopRecording()
                uploadVoiceMessage(audioFile, bearerToken, groupId)
            }
        }
    }

    private fun checkMicPermission(): Boolean {
        return checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun startRecording() {
        val timestamp = System.currentTimeMillis()
        val fileName = "voice_$timestamp.3gp"
        audioFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }

        isRecording = true
        Toast.makeText(this, "Nagrywanie rozpoczęte...", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
        Toast.makeText(this, "Nagrywanie zakończone", Toast.LENGTH_SHORT).show()
    }

    private fun uploadVoiceMessage(file: File, token: String, groupId: Int) {
        val requestFile = file.asRequestBody("audio/3gpp".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.uploadAttachment(token, body)
                val filename = response["attachment"] ?: ""

                val group = Group(id = groupId, name = "")
                val message = Message(
                    id = null,
                    group = group,
                    sender = null,
                    content = "",
                    sent_at = null,
                    attachment = filename,
                    status = null
                )

                RetrofitClient.apiService.sendMessage(token, message)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChatActivity, "Wysłano głosówkę", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("UPLOAD ERROR", "Błąd: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChatActivity, "Błąd przy wysyłaniu głosowej", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Powiedz coś...")

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Urządzenie nie obsługuje rozpoznawania mowy", Toast.LENGTH_SHORT).show()
        }
    }
}
