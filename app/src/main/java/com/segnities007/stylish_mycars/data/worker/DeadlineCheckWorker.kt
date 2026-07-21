package com.segnities007.stylish_mycars.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.segnities007.stylish_mycars.data.local.AppDatabase
import com.segnities007.stylish_mycars.data.local.entity.VehicleEntity
import com.segnities007.stylish_mycars.data.notification.NotificationHelper
import com.segnities007.stylish_mycars.domain.model.MaintenanceCategory
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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

            val inspectionExpiry = vehicle.firstRegistrationDate?.let {
                com.segnities007.stylish_mycars.domain.service.InspectionCalculator.calculateCurrentExpiry(
                    it,
                    today
                )
            } ?: vehicle.inspectionExpiry
            inspectionExpiry?.let { expiry ->
                val days = ChronoUnit.DAYS.between(today, expiry)
                if (days == 90L || days == 30L || days == 14L || days == 7L || days < 0) {
                    val text = if (days < 0) "$name の車検が期限切れです（$expiry）"
                    else "$name の車検まであと${days}日です（$expiry）"
                    NotificationHelper.showNotification(
                        applicationContext, NotificationHelper.CHANNEL_INSPECTION,
                        notificationId++, "車検のお知らせ", text,
                    )
                }
            }

            vehicle.jibaiExpiry?.let { expiry ->
                val days = ChronoUnit.DAYS.between(today, expiry)
                if (days == 30L || days == 14L || days == 7L || days < 0) {
                    val text = if (days < 0) "$name の自賠責保険が期限切れです"
                    else "$name の自賠責保険まであと${days}日です"
                    NotificationHelper.showNotification(
                        applicationContext, NotificationHelper.CHANNEL_INSPECTION,
                        notificationId++, "自賠責保険のお知らせ", text,
                    )
                }
            }

            vehicle.insuranceExpiry?.let { expiry ->
                val days = ChronoUnit.DAYS.between(today, expiry)
                if (days == 30L || days == 14L || days < 0) {
                    val text = if (days < 0) "$name の任意保険が期限切れです"
                    else "$name の任意保険まであと${days}日です"
                    NotificationHelper.showNotification(
                        applicationContext, NotificationHelper.CHANNEL_INSURANCE,
                        notificationId++, "任意保険のお知らせ", text,
                    )
                }
            }

            if (today.monthValue == 5 && today.dayOfMonth <= 7 && !vehicle.taxPaid) {
                NotificationHelper.showNotification(
                    applicationContext, NotificationHelper.CHANNEL_TAX,
                    notificationId++, "自動車税の納付",
                    "$name の自動車税の納付時期です",
                )
            }
        }

        // 整備スケジュールチェック
        val schedules = db.maintenanceScheduleDao()
            .getAll()
        val latestOdometers = mutableMapOf<Long, Int>()
        for (vehicle in vehicleList) {
            val latestFuel = db.fuelRecordDao()
                .getByVehicleId(vehicle.id)
                .first()
                .firstOrNull()
            latestOdometers[vehicle.id] = latestFuel?.odometer ?: 0
        }

        for (schedule in schedules) {
            val vehicle = vehicleList.find { it.id == schedule.vehicleId } ?: continue
            val name = "${vehicle.maker} ${vehicle.name}"
            val currentOdo = latestOdometers[schedule.vehicleId] ?: 0
            val categoryLabel = try {
                MaintenanceCategory.valueOf(schedule.category).label
            } catch (_: Exception) {
                schedule.category
            }

            schedule.intervalMonths?.let { interval ->
                schedule.lastDoneDate?.let { lastDone ->
                    val nextDue = lastDone.plusMonths(interval.toLong())
                    val daysUntil = ChronoUnit.DAYS.between(today, nextDue)
                    if (daysUntil <= 7) {
                        val text = if (daysUntil < 0) "$name の$categoryLabel の時期を過ぎています"
                        else "$name の$categoryLabel まであと${daysUntil}日です"
                        NotificationHelper.showNotification(
                            applicationContext, NotificationHelper.CHANNEL_MAINTENANCE,
                            notificationId++, "メンテナンスのお知らせ", text,
                        )
                    }
                }
            }

            schedule.intervalKm?.let { interval ->
                schedule.lastDoneOdometer?.let { lastOdo ->
                    val remaining = (lastOdo + interval) - currentOdo
                    if (remaining <= 500) {
                        val text = if (remaining <= 0) {
                            "$name の$categoryLabel の距離を超えています（${
                                String.format(
                                    "%,d",
                                    currentOdo
                                )
                            }km）"
                        }
                        else {
                            "$name の$categoryLabel まであと${
                                String.format(
                                    "%,d",
                                    remaining
                                )
                            }kmです"
                        }
                        NotificationHelper.showNotification(
                            applicationContext, NotificationHelper.CHANNEL_MAINTENANCE,
                            notificationId++, "メンテナンスのお知らせ", text,
                        )
                    }
                }
            }
        }

        return Result.success()
    }
}
