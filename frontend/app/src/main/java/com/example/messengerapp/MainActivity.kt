package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var loginBtn : Button
    private lateinit var registerBtn : androidx.appcompat.widget.AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        if (token != null) {
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_main)
        }

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerBtn = findViewById(R.id.register_btn)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val editor = sharedPref.edit()

            Log.i("Test Credentials", "Username : $username and Password : $password")
            val userLogin = UserLogin(
                username = username,
                password = password
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tokenReceived = RetrofitClient.apiService.loginUser(userLogin)
                    editor.putString("auth_token", tokenReceived)
                    editor.apply()
                    Log.i("API SUCCESS", "Logged in as: $tokenReceived")
                    val intent = Intent(this@MainActivity, ChatListActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("API ERROR", "Failed to log in: ${e.message}")
                }
            }
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}