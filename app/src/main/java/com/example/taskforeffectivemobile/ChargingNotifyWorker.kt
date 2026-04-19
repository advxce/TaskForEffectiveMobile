package com.example.taskforeffectivemobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class ChargingNotifyWorker(
    private val appContext: Context,
    private val params: WorkerParameters) :
   Worker(appContext, params) {
    override fun doWork(): Result {
        notifyAboutDeviceOnCharge()
        println("start")
        return Result.success()
    }

    fun notifyAboutDeviceOnCharge(){
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "channel_id"

        val notificationChannel = NotificationChannel(
            channelId,
            "ChargeNotifyChannel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle("Device charging")
            .setContentText("Your device charging right now")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)

        println("work")
    }

}