package com.example.taskforeffectivemobile

import android.app.Application
import android.content.Intent
import android.os.Build
import com.example.taskforeffectivemobile.simularLocation.AndroidGPSLocation
import com.example.taskforeffectivemobile.simularLocation.AndroidGPSLocationService

class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Запускаем сервис
        val intent = Intent(this, AndroidGPSLocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    fun getLocationProvider(): AndroidGPSLocation? {
        return AndroidGPSLocationService.instance?.getLocationProvider()
    }
}