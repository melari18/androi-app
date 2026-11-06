package com.example.lab12

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var btnPlayPause: Button
    private lateinit var btnStop: Button
    private lateinit var seekBar: SeekBar
    private lateinit var textTime: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnStop = findViewById(R.id.btnStop)
        seekBar = findViewById(R.id.seekBar)
        textTime = findViewById(R.id.textTime)

        mediaPlayer = MediaPlayer.create(this, R.raw.dimon)
        seekBar.max = mediaPlayer?.duration ?: 0

        btnPlayPause.setOnClickListener {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    btnPlayPause.text = getString(R.string.play)
                } else {
                    if (player.currentPosition >= player.duration) {
                        player.seekTo(0)
                        seekBar.progress = 0
                    }
                    player.start()
                    btnPlayPause.text = getString(R.string.pause)
                    updateSeekBar()
                }
            }
        }

        btnStop.setOnClickListener {
            mediaPlayer?.let { player ->
                player.pause()
                player.seekTo(0)
                btnPlayPause.text = getString(R.string.play)
                seekBar.progress = 0
                updateTimeText(0, player.duration)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    updateTimeText(progress, mediaPlayer?.duration ?: 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
            }
        })
    }

    // ===== ДОБАВЛЯЕМ МЕНЮ =====
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("Лаба 12: Работа с аудио\n\nАвтор: Мельничук Арина, гр. БОМ41ПРИ")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying && !isUserSeeking) {
            val currentPosition = mediaPlayer!!.currentPosition
            seekBar.progress = currentPosition
            updateTimeText(currentPosition, mediaPlayer!!.duration)
            if (currentPosition >= mediaPlayer!!.duration) {
                btnPlayPause.text = getString(R.string.play)
            }
        }
        handler.postDelayed({ updateSeekBar() }, 100)
    }

    private fun updateTimeText(currentMs: Int, totalMs: Int) {
        val currentSec = currentMs / 1000
        val totalSec = totalMs / 1000
        val currentTimeString = String.format("%02d:%02d", currentSec / 60, currentSec % 60)
        val totalTimeString = String.format("%02d:%02d", totalSec / 60, totalSec % 60)
        textTime.text = "$currentTimeString / $totalTimeString"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}