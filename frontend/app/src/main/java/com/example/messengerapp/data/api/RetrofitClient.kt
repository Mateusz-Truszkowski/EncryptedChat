package com.example.messengerapp.data.api

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.*

@SuppressLint("StaticFieldLeak")
object RetrofitClient {

    private const val BASE_URL = "https://10.0.2.2:8443"
    private var context: Context? = null

    fun init(applicationContext: Context) {
        context = applicationContext
    }

    private fun createSecureClient(context: Context): OkHttpClient {
        try {
            val password = "1234".toCharArray()

            val clientKeyStore = KeyStore.getInstance("PKCS12").apply {
                val clientInput = context.assets.open("client.p12")
                load(clientInput, password)
            }

            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
                init(clientKeyStore, password)
            }

            val trustStore = KeyStore.getInstance("PKCS12").apply {
                val trustInput = context.assets.open("truststore.p12")
                load(trustInput, "123456".toCharArray())
            }

            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(trustStore)
            }

            val trustManager = tmf.trustManagers.first { it is X509TrustManager } as X509TrustManager

            val sslContext = SSLContext.getInstance("TLS").apply {
                init(kmf.keyManagers, tmf.trustManagers, SecureRandom())
            }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to create secure OkHttpClient", e)
        }
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createSecureClient(context!!))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
