package com.example.taskforeffectivemobile.simularLocation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import java.util.Locale
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AndroidGPSLocationService : Service() {

    private lateinit var locationProvider: AndroidGPSLocation
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        var instance: AndroidGPSLocationService? = null
            private set

        // Флаг, остановлен ли сервис вручную
        var isStoppedManually = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        isStoppedManually = false

        locationProvider = AndroidGPSLocation(applicationContext)
        createNotificationChannel()
        startForeground(1, createNotification())

        Log.d("GPS_SERVICE", "Сервис создан и запущен")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Если сервис остановлен вручную - не перезапускаем
        if (isStoppedManually) {
            return START_NOT_STICKY
        }

        serviceScope.launch {
            try {
                locationProvider.startBackgroundUpdates()
                Log.d("GPS_SERVICE", "Обновления геолокации запущены")
                
                // Подписываемся на обновления местоположения
                locationProvider.getLocation().collect { result ->
                    result.onSuccess { point ->
                        updateNotification(point)
                    }
                }
            } catch (e: SecurityException) {
                Log.e("GPS_SERVICE", "Ошибка разрешений: ${e.message}")
            }
        }

        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopServiceManually()
    }

    override fun onDestroy() {
        Log.d("GPS_SERVICE", "Сервис уничтожается")
        super.onDestroy()

        serviceScope.launch {
            locationProvider.stopBackgroundUpdates()
        }
        serviceScope.cancel()

        if (!isStoppedManually) {
            isStoppedManually = true
        }
        instance = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Служба геолокации",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Уведомления о работе GPS в фоне"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("GPS_SERVICE", "Канал уведомлений создан")
        }
    }

    private fun createNotification(contentText: String? = null): Notification {
        val text = contentText ?: "Приложение отслеживает местоположение для работы карты"
        return NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Ваше местоположение")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(point: Point) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val text = "Координаты: ${String.format(Locale.US, "%.5f", point.latitude)}, ${String.format(Locale.US, "%.5f", point.longitude)}"
        notificationManager.notify(1, createNotification(text))
    }

    fun getLocationProvider(): AndroidGPSLocation = locationProvider

    fun stopServiceManually() {
        isStoppedManually = true
        stopSelf()
    }
}