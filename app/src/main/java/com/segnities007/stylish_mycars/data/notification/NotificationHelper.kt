package com.segnities007.stylish_mycars.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.segnities007.stylish_mycars.R

object NotificationHelper {
    const val CHANNEL_INSPECTION = "vehicle_inspection"
    const val CHANNEL_INSURANCE = "insurance"
    const val CHANNEL_TAX = "tax"
    const val CHANNEL_MAINTENANCE = "maintenance"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel(CHANNEL_INSPECTION, "車検・自賠責", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "車検・自賠責保険の期限通知" },
            NotificationChannel(CHANNEL_INSURANCE, "任意保険", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "任意保険の満期通知" },
            NotificationChannel(CHANNEL_TAX, "税金", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "自動車税の納付通知" },
            NotificationChannel(CHANNEL_MAINTENANCE, "メンテナンス", NotificationManager.IMPORTANCE_LOW)
                .apply { description = "整備の目安通知" },
        )
        channels.forEach { manager.createNotificationChannel(it) }
    }

    fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        text: String,
    ) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }
}
