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
import com.example.messengerapp.data.model.Group
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
        val searchView = SearchView(this).apply {
            queryHint = "Wpisz nazwę użytkownika"
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text)?.inputType = InputType.TYPE_CLASS_TEXT
        }

        val token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("auth_token", null)
        val bearerToken = "Bearer $token"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Dodaj nowy czat")
            .setView(searchView)
            .setPositiveButton("Szukaj") { dialog, _ ->
                val query = searchView.query.toString().trim()

                if (query.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val users = RetrofitClient.apiService.getAllUsers(bearerToken)
                            val userToAdd = users.find { it.username.equals(query, ignoreCase = true) }

                            if (userToAdd != null) {
                                val newGroup = RetrofitClient.apiService.createGroup(bearerToken, Group(null, "Test group"))

                                RetrofitClient.apiService.addUserToGroup(
                                    newGroup.id!!,
                                    userToAdd,
                                    bearerToken
                                )

                                withContext(Dispatchers.Main) {
                                    val intent = Intent(this@ChatListActivity, ChatActivity::class.java)
                                    intent.putExtra("group_id", newGroup.id)
                                    intent.putExtra("group_name", "Nowy czat")
                                    startActivity(intent)
                                }

                                dialog.dismiss()
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@ChatListActivity, "Nie znaleziono użytkownika", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Log.e("API ERROR", "Błąd: ${e.message}")
                                Toast.makeText(this@ChatListActivity, "Błąd podczas tworzenia czatu", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Wprowadź nazwę użytkownika", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Anuluj") { dialog, _ ->
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