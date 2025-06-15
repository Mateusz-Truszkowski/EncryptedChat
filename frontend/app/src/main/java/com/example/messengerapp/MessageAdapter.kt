package com.example.messengerapp

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.data.api.ApiService
import com.example.messengerapp.data.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class MessageAdapter(
    private var messages: List<Message>,
    private val currentUserUsername: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        val senderUsername = messages[position].sender?.username
        return if (senderUsername == currentUserUsername) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutId = if (viewType == VIEW_TYPE_SENT) R.layout.message_item_sent else R.layout.message_item_received
        val view = inflater.inflate(layoutId, parent, false)
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(view)
        } else {
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    fun updateMessages(newMessages: List<Message>) {
        (messages as? MutableList)?.let {
            it.clear()
            it.addAll(newMessages)
            notifyDataSetChanged()
        }
    }

    fun createRetrofit(context: Context): Retrofit {
        val keyStore = KeyStore.getInstance("PKCS12")
        context.resources.openRawResource(R.raw.client).use { fis ->
            keyStore.load(fis, "1234".toCharArray())
        }
        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(keyStore, "1234".toCharArray())

        val trustStore = KeyStore.getInstance("PKCS12")
        context.resources.openRawResource(R.raw.truststore).use { fis ->
            trustStore.load(fis, "123456".toCharArray())
        }
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(trustStore)

        val trustManager = tmf.trustManagers[0] as X509TrustManager

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(kmf.keyManagers, arrayOf(trustManager), null)

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://10.0.2.2:8443/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    private fun playAudio(context: Context, filename: String) {
        val token = getAuthToken(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = createRetrofit(context)
                val api = retrofit.create(ApiService::class.java)
                val response = api.downloadAttachment("Bearer $token", filename)

                if (response.isSuccessful) {
                    val inputStream = response.body()?.byteStream()
                    val tempFile = File.createTempFile("voice_", ".3gp", context.cacheDir)
                    tempFile.outputStream().use { output -> inputStream?.copyTo(output) }

                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(tempFile.absolutePath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Odtwarzanie głosówki...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Błąd pobierania: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Błąd odtwarzania: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("PLAYER", "Error", e)
                }
            }
        }
    }



    private fun getAuthToken(context: Context): String {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("auth_token", "") ?: ""
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_sent)
        private val playButton: TextView = itemView.findViewById(R.id.play_button_sent)


        fun bind(message: Message) {
            // Ukrywamy puste wiadomości tekstowe
            if (message.content.isNullOrBlank()) {
                messageTextView.visibility = View.GONE
            } else {
                messageTextView.text = message.content
                messageTextView.visibility = View.VISIBLE
            }

            // Pokazujemy przycisk odtwarzania tylko jeśli jest załącznik audio
            if (message.attachment != null && message.attachment.endsWith(".3gp")) {
                playButton.visibility = View.VISIBLE
                playButton.setOnClickListener {
                    playAudio(itemView.context, message.attachment)
                }
            } else {
                playButton.visibility = View.GONE
            }
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_received)
        private val playButton: TextView = itemView.findViewById(R.id.play_button_received)


        fun bind(message: Message) {
            if (message.content.isNullOrBlank()) {
                messageTextView.visibility = View.GONE
            } else {
                messageTextView.text = message.content
                messageTextView.visibility = View.VISIBLE
            }

            if (message.attachment != null && message.attachment.endsWith(".3gp")) {
                playButton.visibility = View.VISIBLE
                playButton.setOnClickListener {
                    playAudio(itemView.context, message.attachment)
                }
            } else {
                playButton.visibility = View.GONE
            }
        }
    }
}
