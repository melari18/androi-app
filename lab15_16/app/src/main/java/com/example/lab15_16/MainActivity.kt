package com.example.lab15_16

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: MyDbHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextRecord: EditText

    // Запрос разрешения на уведомления (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Уведомления запрещены", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = MyDbHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        editTextRecord = findViewById(R.id.editTextRecord)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnGetToken = findViewById<Button>(R.id.btnGetToken)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 1. Загружаем данные из БД при запуске
        loadData()

        // 2. Добавление записи в БД (Лаба 15)
        btnAdd.setOnClickListener {
            val text = editTextRecord.text.toString()
            if (text.isNotEmpty()) {
                dbHelper.addRecord(text)
                editTextRecord.text.clear()
                loadData() // Обновляем список
            }
        }

        // 3. Получение токена Firebase (Лаба 16)
        btnGetToken.setOnClickListener {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Toast.makeText(this, "Токен: $token", Toast.LENGTH_LONG).show()
                    android.util.Log.d("FCM_TOKEN", token)
                } else {
                    Toast.makeText(this, "Ошибка получения токена", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 4. Запрос разрешения на уведомления
        askNotificationPermission()

        // 5. Демонстрация SharedPreferences (настройки из Лабы 15)
        // (В реальном проекте здесь была бы отдельная SettingsActivity,
        // но для совмещения мы просто сохраним настройку при старте)
        val prefs = getSharedPreferences("SAVE", 0)
        val savedText = prefs.getString("text", "Настройка не задана")
        Toast.makeText(this, "Из настроек: $savedText", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        val records = dbHelper.getAllRecords()
        recyclerView.adapter = MyAdapter(records)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}