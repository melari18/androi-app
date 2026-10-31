package com.example.lab8

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NewAppWidgetConfigureActivity : Activity() {

    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.new_app_widget_configure)
        setResult(RESULT_CANCELED)

        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            val context = this
            val widgetManager = AppWidgetManager.getInstance(context)
            val editText = findViewById<EditText>(R.id.appwidget_text)
            val button = findViewById<Button>(R.id.add_button)

            // ЗАГРУЖАЕМ ССЫЛКУ: если она есть, ставим в поле. Если нет — оставляем пустым.
            val prefs = context.getSharedPreferences("com.example.lab8.NewAppWidget", 0)
            val currentUrl = prefs.getString("appwidget_$widgetID", "")
            if (!currentUrl.isNullOrEmpty()) {
                editText.setText(currentUrl)
            }
            // Если currentUrl пустой, поле останется таким, каким задано в XML (например, с https://)

            button.setOnClickListener {
                val newUrl = editText.text.toString()

                // Сохраняем ссылку
                val editor = context.getSharedPreferences("com.example.lab8.NewAppWidget", 0).edit()
                editor.putString("appwidget_$widgetID", newUrl)
                editor.apply()

                // Обновляем виджет сразу
                updateAppWidget(context, widgetManager, widgetID)

                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
                setResult(RESULT_OK, resultValue)
                finish()
            }
        }
    }
}

// Вспомогательные функции можно оставить здесь (или удалить, если они больше не нужны)
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences("com.example.lab8.NewAppWidget", 0).edit()
    prefs.putString("appwidget_$appWidgetId", text)
    prefs.apply()
}

internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences("com.example.lab8.NewAppWidget", 0)
    return prefs.getString("appwidget_$appWidgetId", "") ?: ""
}