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

        // Регистрируем ОБА TextView для контекстного меню
        val helloText = findViewById<TextView>(R.id.text1)
        val labText = findViewById<TextView>(R.id.text2)

        registerForContextMenu(helloText)
        registerForContextMenu(labText)
    }

    // ===== ОПЦИОНАЛЬНОЕ МЕНЮ (три точки) =====
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

        when (v.id) {
            R.id.text1 -> {
                menuInflater.inflate(R.menu.contextmenu2, menu)
            }
            R.id.text2 -> {
                menuInflater.inflate(R.menu.contextmenu, menu)
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        val helloText = findViewById<TextView>(R.id.text1)
        val labText = findViewById<TextView>(R.id.text2)

        if (id == R.id.color_black) {
            helloText.setTextColor(Color.BLACK)
            return true
        }
        if (id == R.id.color_red) {
            helloText.setTextColor(Color.RED)
            return true
        }

        if (id == R.id.color_blue) {
            labText.setTextColor(Color.BLUE)
            return true
        }
        if (id == R.id.color_green) {
            labText.setTextColor(Color.GREEN)
            return true
        }

        return super.onContextItemSelected(item)
    }

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