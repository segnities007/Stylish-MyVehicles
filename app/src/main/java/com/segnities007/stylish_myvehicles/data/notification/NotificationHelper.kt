package com.segnities007.stylish_myvehicles.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.segnities007.stylish_myvehicles.R

object NotificationHelper {
    const val CHANNEL_INSPECTION = "vehicle_inspection"
    const val CHANNEL_INSURANCE = "insurance"
    const val CHANNEL_TAX = "tax"
    const val CHANNEL_MAINTENANCE = "maintenance"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel(
                CHANNEL_INSPECTION,
                context.getString(R.string.channel_inspection_name),
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply { description = context.getString(R.string.channel_inspection_description) },
            NotificationChannel(
                CHANNEL_INSURANCE,
                context.getString(R.string.channel_insurance_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply { description = context.getString(R.string.channel_insurance_description) },
            NotificationChannel(CHANNEL_TAX, context.getString(R.string.channel_tax_name), NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = context.getString(R.string.channel_tax_description) },
            NotificationChannel(
                CHANNEL_MAINTENANCE,
                context.getString(R.string.channel_maintenance_name),
                NotificationManager.IMPORTANCE_LOW
            )
                .apply { description = context.getString(R.string.channel_maintenance_description) },
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
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }
}
