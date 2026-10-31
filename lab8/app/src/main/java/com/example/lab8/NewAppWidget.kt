package com.example.lab8

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class NewAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Получаем сохраненную ссылку (или пустую строку, если ее нет)
    val prefs = context.getSharedPreferences("com.example.lab8.NewAppWidget", 0)
    val savedUrl = prefs.getString("appwidget_$appWidgetId", "") ?: ""

    val views = RemoteViews(context.packageName, R.layout.new_app_widget)

    // 1. Убираем http:// и https:// для красивого отображения
    val displayUrl = savedUrl.replace("https://", "").replace("http://", "")
    views.setTextViewText(R.id.appwidget_text, displayUrl)

    // 2. Клик по тексту -> открыть сайт (если ссылка есть)
    if (savedUrl.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(savedUrl))
        val pending = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.appwidget_text, pending)
    }

    // 3. Клик по кнопке ✎ -> открыть настройки
    val intentEdit = Intent(context, NewAppWidgetConfigureActivity::class.java)
    intentEdit.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    val pendingEdit = PendingIntent.getActivity(
        context, appWidgetId + 1, intentEdit,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.btn_edit, pendingEdit)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}