package com.example.messengerapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.messengerapp.data.api.RetrofitClient
import com.example.messengerapp.data.model.UserLogin
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: androidx.appcompat.widget.AppCompatButton
    private lateinit var langBtn: Button
    private lateinit var themeBtn: Button
    private lateinit var sharedPref: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Ustaw język przed załadowaniem layoutu
        val savedLang = sharedPref.getString("app_language", null)
        if (savedLang != null) {
            setLocale(savedLang, applyNow = false)
        }

        // Ustaw motyw przed załadowaniem widoku
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        // Jeśli użytkownik jest już zalogowany
        val token = sharedPref.getString("auth_token", null)
        if (token != null) {
            startActivity(Intent(this, ChatListActivity::class.java))
            finish()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerBtn = findViewById(R.id.register_btn)
        langBtn = findViewById(R.id.switch_lang_btn)
        themeBtn = findViewById(R.id.theme_button)

        // Obsługa zmiany języka (PL/EN)
        langBtn.setOnClickListener {
            val currentLang = sharedPref.getString("app_language", "en") ?: "en"
            val newLang = if (currentLang == "en") "pl" else "en"
            sharedPref.edit().putString("app_language", newLang).apply()
            setLocale(newLang, applyNow = true)
        }

        // Obsługa zmiany motywu (ciemny/jasny)
        themeBtn.text = getString(if (isDarkMode) R.string.light_mode else R.string.dark_mode)

        themeBtn.setOnClickListener {
            val isCurrentlyDark = sharedPref.getBoolean("dark_mode", false)
            val newMode = !isCurrentlyDark

            AppCompatDelegate.setDefaultNightMode(
                if (newMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            sharedPref.edit().putBoolean("dark_mode", newMode).apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val editor = sharedPref.edit()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val fcmToken = FirebaseMessaging.getInstance().token.await()
                    val userLogin = UserLogin(username = username, password = password, fcmToken = fcmToken)
                    val tokenReceived = RetrofitClient.apiService.loginUser(userLogin)
                    editor.putString("auth_token", tokenReceived.token)
                    editor.putString("username", username)
                    editor.apply()

                    Log.i("API SUCCESS", "Logged in as: $tokenReceived")
                    startActivity(Intent(this@MainActivity, ChatListActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Log.e("API ERROR", "Failed to log in: ${e.message}")
                }
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setLocale(languageCode: String, applyNow: Boolean) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        if (applyNow) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("PERMISSION", "POST_NOTIFICATIONS granted")
            } else {
                Log.w("PERMISSION", "POST_NOTIFICATIONS denied – notifications may not work")
            }
        }
    }
}
