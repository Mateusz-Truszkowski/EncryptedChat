package com.example.messengerapp.data.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class StompClient(val groupId: Int, val token: String, val onMessageReceived: (String) -> Unit) {
    private lateinit var client: WebSocketClient

    fun connect() {
        val url = URI("ws://10.0.2.2:8080/ws/websocket?token=$token")

        client = object : WebSocketClient(url) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("STOMP", "Połączono")

                // STOMP CONNECT
                val connectFrame = "CONNECT\naccept-version:1.2\n\n\u0000"
                send(connectFrame)

                // SUBSCRIBE (po chwili, żeby backend zdążył odpowiedzieć)
                Handler(Looper.getMainLooper()).postDelayed({
                    val subscribeFrame = "SUBSCRIBE\nid:sub-0\ndestination:/topic/group.$groupId\n\n\u0000"
                    send(subscribeFrame)
                }, 500)
            }

            override fun onMessage(message: String?) {
                Log.d("STOMP", "Odebrano wiadomość:\n$message")
                if (message != null && message.contains("MESSAGE")) {
                    val payload = message.substringAfter("\n\n").substringBefore("\u0000")
                    onMessageReceived(payload)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("STOMP", "Rozłączono: $code $reason")
            }

            override fun onError(ex: Exception?) {
                Log.e("STOMP", "Błąd WebSocket", ex)
            }
        }

        client.connect()
    }

    fun disconnect() {
        if (::client.isInitialized && client.isOpen) {
            client.close()
        }
    }
}