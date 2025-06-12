package com.example.messengerapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // dla emulatora Androida

    // Dodaj OkHttpClient z interceptorem
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val modifiedRequest = originalRequest.newBuilder()
                .header("Key", "Content-Type")
                .header("Content-Type", "application/json")// Dodaje nagłówek do wszystkich żądań
                .method(originalRequest.method, originalRequest.body)
                .build()
            chain.proceed(modifiedRequest)
        }
        .addInterceptor(HttpLoggingInterceptor().apply { // Opcjonalnie: logowanie żądań (debug)
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Używa OkHttpClient z interceptorami
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}