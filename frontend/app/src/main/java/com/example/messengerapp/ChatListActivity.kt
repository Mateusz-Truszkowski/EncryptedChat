package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.data.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ChatListActivity : AppCompatActivity() {
    private lateinit var newChat : Button
    private lateinit var logout : Button
    private lateinit var groupRecyclerView: RecyclerView

    fun showSearchDialog() {
        // Tworzenie layoutu dla dialogu
        val searchView = SearchView(this).apply {
            queryHint = "Wpisz numer użytkownika"  // Podpowiedź w polu wyszukiwania

            // Uzyskiwanie dostępu do EditText w SearchView
            val searchEditText = this.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            if (searchEditText != null) {
                searchEditText.inputType = InputType.TYPE_CLASS_NUMBER // Ustawienie inputType na liczby
            } else {
                Log.e("Search", "EditText w SearchView jest null!")
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.user_search))
            .setView(searchView) // Ustawiamy SearchView jako widok dialogu
            .setPositiveButton("Szukaj") { dialog, _ ->
                val queryString = searchView.query.toString()
                val userId = queryString.toIntOrNull()

                // Jeśli konwersja się powiedzie, wykonaj zapytanie do API
                if (userId != null) {
                    Log.i("Search", "Searching for: $userId")

                    // Uruchamiamy zapytanie w tle
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // Wysyłamy zapytanie do API, aby pobrać dane użytkownika
                            val user = userId?.let { RetrofitClient.apiService.getUser(it) }
                            if (user != null) {
                                Log.i("API SUCCESS", "Found user: ${user.username}")

                                dialog.dismiss()
                                // Wracamy na główny wątek, aby zaktualizować UI
                                withContext(Dispatchers.Main) {
                                    val intent = Intent(this@ChatListActivity, ChatActivity::class.java)
                                    intent.putExtra("username", user.username)
                                    intent.putExtra("receiverId", user.id)
                                    startActivity(intent)
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@ChatListActivity, "Użytkownik nie został znaleziony.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            // Błąd w zapytaniu
                            withContext(Dispatchers.Main) {
                                Log.e("API ERROR", "Failed to retrieve user: ${e.message}")
                                Toast.makeText(this@ChatListActivity, getString(R.string.user_doesnt_exist), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Jeśli wprowadzone dane nie są liczbą
                    Toast.makeText(this, getString(R.string.incorrect_user), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun setLocale(localeCode: String) {
        val locale = Locale(localeCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        createConfigurationContext(config)
        recreate() // Przeładuj Activity, aby zaktualizować język
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        newChat = findViewById(R.id.newChat_btn)
        logout = findViewById(R.id.logout_btn)
        groupRecyclerView = findViewById(R.id.chat_list)

        groupRecyclerView.layoutManager = LinearLayoutManager(this)

        newChat.setOnClickListener {
            showSearchDialog()
        }

        logout.setOnClickListener {
            val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPref.edit().remove("auth_token").apply()
            startActivity(Intent(this, MainActivity::class.java))
        }

        fetchGroups()
    }

    private fun fetchGroups() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null)
                val bearerToken = "Bearer $token"

                val response = RetrofitClient.apiService.getGroups(bearerToken)
                withContext(Dispatchers.Main) {
                    groupRecyclerView.adapter = GroupAdapter(response) { group ->
                        val intent = Intent(this@ChatListActivity, ChatActivity::class.java)
                        intent.putExtra("group_id", group.id)
                        intent.putExtra("group_name", group.name)
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Log.e("API ERROR", "Failed to fetch groups: ${e.message}")
            }
        }
    }
}