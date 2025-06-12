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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailInput : EditText
    private lateinit var usernameInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var registerSubmitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailInput = findViewById(R.id.emailRegister_input)
        usernameInput = findViewById(R.id.usernameRegister_input)
        passwordInput = findViewById(R.id.passwordRegister_input)
        firstNameInput = findViewById(R.id.firstNameRegister_input)
        lastNameInput = findViewById(R.id.lastNameRegister_input)
        registerSubmitBtn = findViewById(R.id.registerSubmit_btn)

        registerSubmitBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = sharedPref.edit()

            val newUser = User(
                id = 0,
                username = username,
                password = password,
                role = "user",
                last_activity = null
            )

            val userLogin = UserLogin(
                username = username,
                password = password
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.createUser(newUser)
                    Log.i("API SUCCESS", "User created: $response")

                    val token = RetrofitClient.apiService.loginUser(userLogin)
                    editor.putString("auth_token", token.token)
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
