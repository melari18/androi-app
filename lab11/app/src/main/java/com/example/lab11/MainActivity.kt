package com.example.lab11

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val channelId = "my_channel_id"

    // Регистрируем запрос разрешения на уведомления (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showNotification()
        } else {
            // Пользователь отказал — можно показать Toast
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Создаём канал уведомлений (обязательно для Android 8+)
        createNotificationChannel()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            checkPermissionAndShow()
        }
    }

    // Проверяем разрешение и показываем уведомление
    private fun checkPermissionAndShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Запрашиваем разрешение
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showNotification()
            }
        } else {
            showNotification()
        }
    }

    // Создание канала уведомлений
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText

                // Звук для канала
                val soundUri = Uri.parse("android.resource://${packageName}/${R.raw.sound_11344}")
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Показ уведомления
    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Создаём уведомление
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(getString(R.string.notif_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(Uri.parse("android.resource://${packageName}/${R.raw.sound_11344}"))

        // Показываем уведомление
        try {
            NotificationManagerCompat.from(this).notify(1, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // Создание меню
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Обработка нажатия на пункт меню
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("Лаба 11: Работа с уведомлениями\n\nАвтор: Мельничук Арина, гр. БОМ41ПРИ")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setIcon(R.mipmap.ic_launcher_round)
                .create()
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}