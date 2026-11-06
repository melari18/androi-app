package com.example.lab13

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var btnLocal: Button
    private lateinit var btnInternet: Button
    private lateinit var seekBar: SeekBar
    private lateinit var textTime: TextView
    private lateinit var btnPlayPause: Button

    private val LOCAL_URI = "android.resource://com.example.lab13/" + R.raw.local_video
    private val INTERNET_URI = "https://www.w3schools.com/html/mov_bbb.mp4"

    private var isLocalVideo = true
    private var savedPosition = 0
    private var isUserSeeking = false

    // Переменная, которая будет запоминать текущую секунду
    private var lastKnownPosition = 0

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        btnLocal = findViewById(R.id.btnLocal)
        btnInternet = findViewById(R.id.btnInternet)
        seekBar = findViewById(R.id.seekBar)
        textTime = findViewById(R.id.textTime)
        btnPlayPause = findViewById(R.id.btnPlayPause)

        // Если приложение пересоздается (поворот экрана)
        if (savedInstanceState != null) {
            isLocalVideo = savedInstanceState.getBoolean("isLocal", true)
            savedPosition = savedInstanceState.getInt("position", 0)
        } else {
            // Если это первый запуск
            loadPositionFromPrefs()
        }

        // Восстанавливаем последнюю известную позицию
        lastKnownPosition = savedPosition

        videoView.setOnPreparedListener { mp ->
            seekBar.max = videoView.duration
            updateTimeText(savedPosition, videoView.duration)

            if (savedPosition > 0) {
                // Если есть позиция для восстановления, перематываем
                videoView.seekTo(savedPosition)

                // Даем 200 мс на то, чтобы перемотка сработала, и только потом включаем
                handler.postDelayed({
                    videoView.start()
                    btnPlayPause.text = "Pause"
                    updateSeekBar()
                }, 200)
            } else {
                // Если позиция 0, запускаем с начала
                videoView.start()
                btnPlayPause.text = "Pause"
                updateSeekBar()
            }
        }

        // Загружаем видео
        playVideo(isLocalVideo)

        // Обработчики кнопок
        btnLocal.setOnClickListener {
            isLocalVideo = true
            savedPosition = 0
            lastKnownPosition = 0
            playVideo(true)
        }

        btnInternet.setOnClickListener {
            isLocalVideo = false
            savedPosition = 0
            lastKnownPosition = 0
            playVideo(false)
        }

        btnPlayPause.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                btnPlayPause.text = "Play"
            } else {
                if (videoView.currentPosition >= videoView.duration) {
                    videoView.seekTo(0)
                }
                videoView.start()
                btnPlayPause.text = "Pause"
                updateSeekBar()
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    videoView.seekTo(progress)
                    lastKnownPosition = progress // Обновляем позицию при перемотке
                    updateTimeText(progress, videoView.duration)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { isUserSeeking = true }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { isUserSeeking = false }
        })
    }

    private fun playVideo(isLocal: Boolean) {
        videoView.stopPlayback()
        if (isLocal) {
            videoView.setVideoURI(Uri.parse(LOCAL_URI))
        } else {
            videoView.setVideoURI(Uri.parse(INTERNET_URI))
        }
    }

    private fun updateSeekBar() {
        if (videoView.isPlaying && !isUserSeeking) {
            val currentPosition = videoView.currentPosition
            seekBar.progress = currentPosition
            updateTimeText(currentPosition, videoView.duration)

            // СОХРАНЯЕМ ТЕКУЩУЮ ПОЗИЦИЮ КАЖДЫЕ 100 МС
            lastKnownPosition = currentPosition

            if (currentPosition >= videoView.duration) {
                btnPlayPause.text = "Play"
            }
        }
        handler.postDelayed({ updateSeekBar() }, 100)
    }

    private fun updateTimeText(currentMs: Int, totalMs: Int) {
        val currentSec = currentMs / 1000
        val totalSec = totalMs / 1000
        val currentStr = String.format("%02d:%02d", currentSec / 60, currentSec % 60)
        val totalStr = String.format("%02d:%02d", totalSec / 60, totalSec % 60)
        textTime.text = "$currentStr / $totalStr"
    }

    // Сохранение при повороте
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем нашу переменную lastKnownPosition, а не currentPosition у VideoView
        outState.putBoolean("isLocal", isLocalVideo)
        outState.putInt("position", lastKnownPosition)
        videoView.pause()
    }

    // Сохранение при выходе в фон (Home)
    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences("VideoPrefs", MODE_PRIVATE).edit()
        prefs.putInt("position", lastKnownPosition)
        prefs.putBoolean("isLocal", isLocalVideo)
        prefs.apply()
        videoView.pause()
    }

    private fun loadPositionFromPrefs() {
        val prefs = getSharedPreferences("VideoPrefs", MODE_PRIVATE)
        savedPosition = prefs.getInt("position", 0)
        isLocalVideo = prefs.getBoolean("isLocal", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}