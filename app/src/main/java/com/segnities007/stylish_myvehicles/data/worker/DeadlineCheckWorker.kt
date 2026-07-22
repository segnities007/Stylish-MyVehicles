package com.segnities007.stylish_myvehicles.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.segnities007.stylish_myvehicles.data.local.AppDatabase
import com.segnities007.stylish_myvehicles.data.local.entity.VehicleEntity
import com.segnities007.stylish_myvehicles.data.notification.NotificationHelper
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class DeadlineCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.create(applicationContext)
        val today = LocalDate.now()
        var notificationId = 1000

        val vehicleList: List<VehicleEntity> = db.vehicleDao()
            .getAll()
            .first()

        for (vehicle in vehicleList) {
            val name = "${vehicle.maker} ${vehicle.name}"

            if (today.monthValue == 5 && today.dayOfMonth <= 7) {
                val taxPaidThisYear = db.costRecordDao()
                    .getByVehicleId(vehicle.id)
                    .first()
                    .any { it.category == CostCategory.TAX.name && it.date.year == today.year }
                if (!taxPaidThisYear) {
                    NotificationHelper.showNotification(
                        applicationContext, NotificationHelper.CHANNEL_TAX,
                        notificationId++, "自動車税の納付",
                        "$name の自動車税の納付時期です",
                    )
                }
            }
        }

        return Result.success()
    }
}
