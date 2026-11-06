package com.example.lab13

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var radioGroup: RadioGroup
    private lateinit var rbLocal: android.widget.RadioButton
    private lateinit var rbNetwork: android.widget.RadioButton

    // Локальное видео из папки res/raw
    private val localVideoUri: Uri by lazy {
        Uri.parse("android.resource://$packageName/${R.raw.local_video}")
    }

    // Легкое тестовое видео из интернета
    private val networkVideoUri: Uri = Uri.parse("https://www.w3schools.com/html/mov_bbb.mp4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        radioGroup = findViewById(R.id.radioGroup)
        rbLocal = findViewById(R.id.rbLocal)
        rbNetwork = findViewById(R.id.rbNetwork)

        // Настройка контроллера (кнопки Play/Pause/Перемотка)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Обработка ошибок загрузки видео (например, нет интернета)
        videoView.setOnErrorListener { _, what, extra ->
            Toast.makeText(this, "Ошибка загрузки видео! Проверьте интернет.", Toast.LENGTH_LONG).show()
            true
        }

        // Загружаем видео при старте
        // Читаем сохраненную позицию из памяти телефона
        val prefs = getSharedPreferences("VideoPrefs", Context.MODE_PRIVATE)
        val savedPosition = prefs.getInt("last_position", 0)
        val isLocal = prefs.getBoolean("last_is_local", true)

        rbLocal.isChecked = isLocal
        rbNetwork.isChecked = !isLocal

        loadVideo(isLocal, savedPosition, playWhenReady = false)

        // Переключение между локальным видео и интернетом
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val isLocalSelected = checkedId == R.id.rbLocal
            // При смене источника начинаем видео с 0-й секунды
            loadVideo(isLocalSelected, 0, playWhenReady = true)
        }
    }

    private fun loadVideo(isLocal: Boolean, startPosition: Int, playWhenReady: Boolean) {
        val uri = if (isLocal) localVideoUri else networkVideoUri
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener { mp ->
            if (startPosition > 0) {
                videoView.seekTo(startPosition)
            }
            if (playWhenReady) {
                videoView.start()
            }
        }
    }

    // Сохранение позиции при сворачивании приложения или выходе из него
    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences("VideoPrefs", Context.MODE_PRIVATE).edit()
        prefs.putInt("last_position", videoView.currentPosition)
        prefs.putBoolean("last_is_local", rbLocal.isChecked)
        prefs.apply()

        // Ставим на паузу, чтобы звук не играл в фоне
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    // Создание меню
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Обработка нажатия на пункт "О программе"
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("Лаба 13: Работа с видео\n\nАвтор: Мельничук Арина, гр. БОМ41ПРИ")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}