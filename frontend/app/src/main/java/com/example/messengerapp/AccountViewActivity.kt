package com.example.messengerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerapp.data.api.RetrofitClient
import com.example.messengerapp.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountViewActivity : AppCompatActivity() {
    private lateinit var usernameTextView: TextView
    private lateinit var roleTextView: TextView
    private lateinit var deleteAccountBtn: Button
    private lateinit var changePasswordBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_view)

        usernameTextView = findViewById(R.id.username_display)
        roleTextView = findViewById(R.id.role_display)
        deleteAccountBtn = findViewById(R.id.delete_account_btn)

        val token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null)
        val username = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("username", null)
        Log.d("USERNAME", username.toString());

        usernameTextView.text = getString(R.string.username_format, username)
        if (token != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = RetrofitClient.apiService.getUser(username!!, "Bearer $token")
                    withContext(Dispatchers.Main) {
                        roleTextView.text = getString(R.string.role_format, user.role)
                    }
                } catch (e: Exception) {
                    Log.e("AccountView", "Failed to fetch user info: ${e.message}")
                }
            }
        }

        deleteAccountBtn.setOnClickListener {
            if (token != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitClient.apiService.deleteUser(username!!, "Bearer $token")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AccountViewActivity, R.string.account_deleted, Toast.LENGTH_SHORT).show()
                            getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                            val intent = Intent(this@AccountViewActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            sharedPref.edit().remove("auth_token").apply()
                            startActivity(Intent(this@AccountViewActivity, MainActivity::class.java))
                            finish()
                        }
                    } catch (e: Exception) {
                        Log.e("DeleteUser", "Failed to delete user: ${e.message}")
                    }
                }
            }
        }
    }
}
