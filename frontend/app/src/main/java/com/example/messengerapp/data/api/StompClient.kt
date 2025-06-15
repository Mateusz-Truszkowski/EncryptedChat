import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory

class StompClient(val groupId: Int, val token: String, val onMessageReceived: (String) -> Unit) {
    private lateinit var client: WebSocketClient

    fun connect(context: Context) {
        val url = URI("wss://10.0.2.2:8443/ws/websocket?token=$token")

        val password = "123456".toCharArray()

        val trustStore = KeyStore.getInstance("PKCS12")
        context.assets.open("truststore.p12").use { input ->
            trustStore.load(input, password)
        }
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(trustStore)

        val keyStore = KeyStore.getInstance("PKCS12")
        context.assets.open("client.p12").use { input ->
            keyStore.load(input, "1234".toCharArray())
        }
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, "1234".toCharArray())

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)

        client = object : WebSocketClient(url) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("STOMP", "Połączono")

                val connectFrame = "CONNECT\naccept-version:1.2\nhost:localhost\n\n\u0000"
                send(connectFrame)

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

        client.setSocket(sslContext.socketFactory.createSocket())

        client.connect()
    }

    fun disconnect() {
        if (::client.isInitialized && client.isOpen) {
            client.close()
        }
    }
}
