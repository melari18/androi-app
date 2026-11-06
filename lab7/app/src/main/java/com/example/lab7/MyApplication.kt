package com.example.lab7

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("b9c725a9-3295-4a06-a112-80341f582782")
    }
}