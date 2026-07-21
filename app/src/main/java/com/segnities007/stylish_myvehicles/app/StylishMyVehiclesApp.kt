package com.segnities007.stylish_myvehicles.app

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.segnities007.stylish_myvehicles.app.di.appModule
import com.segnities007.stylish_myvehicles.data.notification.NotificationHelper
import com.segnities007.stylish_myvehicles.data.worker.DeadlineCheckWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class StylishMyVehiclesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StylishMyVehiclesApp)
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
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "deadline_check",
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
    }
}
