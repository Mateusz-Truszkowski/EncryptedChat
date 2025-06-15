package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerapp.data.api.RetrofitClient
import com.example.messengerapp.data.model.User
import com.example.messengerapp.data.model.UserLogin
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var registerSubmitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameInput = findViewById(R.id.usernameRegister_input)
        passwordInput = findViewById(R.id.passwordRegister_input)
        registerSubmitBtn = findViewById(R.id.registerSubmit_btn)

        registerSubmitBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = sharedPref.edit()

            val newUser = User(
                id = null,
                username = username,
                password = password,
                role = "user",
                last_activity = null
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val fcmToken = FirebaseMessaging.getInstance().token.await()
                    val userLogin = UserLogin(
                        username = username,
                        password = password,
                        fcmToken = fcmToken
                    )
                    val response = RetrofitClient.apiService.createUser(newUser)
                    Log.i("API SUCCESS", "User created: $response")

                    val token = RetrofitClient.apiService.loginUser(userLogin)
                    editor.putString("auth_token", token.token)
                    editor.putString("username", username)
                    editor.apply()
                    Log.i("API SUCCESS", "Logged in as: $token")
                    val intent = Intent(this@RegisterActivity, ChatListActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("API ERROR", "Failed to create user/log in: ${e.message}")
                }
            }
        }
    }
}
