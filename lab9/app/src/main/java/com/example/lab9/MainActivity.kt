package com.example.lab9

import android.graphics.Color
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Регистрируем TextView для контекстного меню (долгое нажатие)
        val textView = findViewById<TextView>(R.id.text1)
        registerForContextMenu(textView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.show_text) {
            val textView = findViewById<TextView>(R.id.text1)
            if (item.isChecked) {
                textView.visibility = View.VISIBLE
                item.isChecked = false
            } else {
                textView.visibility = View.INVISIBLE
                item.isChecked = true
            }
            return true
        }

        if (id == R.id.about) {
            showAboutDialog()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.contextmenu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val textView = findViewById<TextView>(R.id.text1)

        if (id == R.id.color_red) {
            textView.setTextColor(Color.parseColor("red"))
            return true
        }
        if (id == R.id.color_black) {
            textView.setTextColor(Color.parseColor("black"))
            return true
        }

        return super.onContextItemSelected(item)
    }

    // ===== ДИАЛОГ «О ПРОГРАММЕ» =====

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("О программе")
            .setMessage(
                        "Опциональное меню: скрытие TextView\n" +
                        "Контекстное меню: изменение цвета текста\n\n" +
                        "Автор: Мельничук Арина, гр. БОМ41ПРИ"
            )
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setIcon(R.mipmap.ic_launcher_round)
            .create()
            .show()
    }
}