package com.segnities007.stylish_mycars.app

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.segnities007.stylish_mycars.app.di.appModule
import com.segnities007.stylish_mycars.data.notification.NotificationHelper
import com.segnities007.stylish_mycars.data.worker.DeadlineCheckWorker
import java.util.concurrent.TimeUnit
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StylishMyCarsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StylishMyCarsApp)
            modules(appModule)
        }
        NotificationHelper.createChannels(this)
        scheduleDeadlineCheck()
    }

    private fun scheduleDeadlineCheck() {
        val request = PeriodicWorkRequestBuilder<DeadlineCheckWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build(),
            )
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "deadline_check",
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
