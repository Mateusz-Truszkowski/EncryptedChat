package com.example.messengerapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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

            val newUser = User(
                id = 69,
                username = username,
                password = password,
                first_name = firstName,
                last_name = lastName,
                email = email
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.createUser(newUser)
                    Log.i("API SUCCESS", "User created: $response")
                } catch (e: Exception) {
                    Log.e("API ERROR", "Failed to create user: ${e.message}")
                }
            }
        }
    }
}
