package com.segnities007.stylish_myvehicles.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.data.notification.NotificationHelper
import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.service.DeadlineResolver
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class DeadlineCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val vehicleRepository: VehicleRepository by inject()
    private val costRecordRepository: CostRecordRepository by inject()

    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        var notificationId = 1000

        val vehicles = vehicleRepository.getAll().first()

        for (vehicle in vehicles) {
            val name = "${vehicle.maker} ${vehicle.name}"

            val deadline = DeadlineResolver.resolve(vehicle, today)
            if (deadline != null && !deadline.isExpired) {
                val days = deadline.daysRemaining
                if (days <= 30) {
                    val channelId = when (deadline.label) {
                        "車検" -> NotificationHelper.CHANNEL_INSPECTION
                        "自賠責保険" -> NotificationHelper.CHANNEL_INSPECTION
                        else -> NotificationHelper.CHANNEL_INSURANCE
                    }
                    NotificationHelper.showNotification(
                        applicationContext, channelId,
                        notificationId++,
                        applicationContext.getString(R.string.deadline_notification_title, deadline.label),
                        applicationContext.getString(R.string.deadline_notification_text, name, deadline.label, deadline.date.toString(), days),
                    )
                }
            }

            if (today.monthValue == 5 && today.dayOfMonth <= 7) {
                val taxPaidThisYear = costRecordRepository
                    .getByVehicleId(vehicle.id)
                    .first()
                    .any { it.category == CostCategory.TAX && it.date.year == today.year }
                if (!taxPaidThisYear) {
                    NotificationHelper.showNotification(
                        applicationContext, NotificationHelper.CHANNEL_TAX,
                        notificationId++, applicationContext.getString(R.string.vehicle_tax_payment_title),
                        applicationContext.getString(R.string.vehicle_tax_payment_text, name),
                    )
                }
            }
        }

        return Result.success()
    }
}
