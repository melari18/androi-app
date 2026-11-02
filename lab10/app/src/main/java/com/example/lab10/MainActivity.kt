package com.example.lab10

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editTextInput)
        val button = findViewById<Button>(R.id.buttonCheck)

        button.setOnClickListener {
            val inputText = editText.text.toString().trim()

            // Проверка корректности
            if (inputText.isEmpty()) {
                showErrorDialog("Поле не может быть пустым! Пожалуйста, введите текст.")
            } else if (inputText.length < 3) {
                showErrorDialog("Текст слишком короткий! Введите хотя бы 3 символа.")
            } else {
                // Всё хорошо
                showSuccessDialog(inputText)
            }
        }
    }

    // Диалог с ошибкой
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Ошибка ввода")
            .setMessage(message)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Понятно") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Диалог с успехом
    private fun showSuccessDialog(text: String) {
        AlertDialog.Builder(this)
            .setTitle("Успешно")
            .setMessage("Вы ввели: $text")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("Лаба 10: Работа с диалоговыми окнами\n\nПроверка корректности ввода текста.\n\nАвтор: Мельничук Арина, гр. БОМ41ПРИ")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setIcon(R.mipmap.ic_launcher_round)
                .create()
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}