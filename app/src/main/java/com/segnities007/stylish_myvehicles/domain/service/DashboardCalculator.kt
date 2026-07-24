package com.segnities007.stylish_myvehicles.domain.service

import com.segnities007.stylish_myvehicles.domain.model.CostCategory
import com.segnities007.stylish_myvehicles.domain.model.CostRecord
import com.segnities007.stylish_myvehicles.domain.model.FuelRecord
import com.segnities007.stylish_myvehicles.domain.model.MaintenanceSchedule
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.MonthlyCostSlice
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehicleDashboard
import java.time.LocalDate

/** ダッシュボード統計を計算するドメインサービス。 */
object DashboardCalculator {

    fun build(
        costs: List<CostRecord>,
        fuels: List<FuelRecord>,
        schedules: List<MaintenanceSchedule>,
    ): VehicleDashboard {
        val now = LocalDate.now()
        val monthlyCost = costs
            .filter { it.date.year == now.year && it.date.month == now.month }
            .sumOf { it.amount }
        val yearlyCost = costs.filter { it.date.year == now.year }
            .sumOf { it.amount }
        val totalCost = costs.sumOf { it.amount }

        val economies = fuels.mapNotNull { it.fuelEconomy }
        val avgEconomy = economies.takeIf { it.isNotEmpty() }
            ?.average()
        val totalDistance = if (fuels.size >= 2) {
            fuels.maxOf { it.odometer } - fuels.minOf { it.odometer }
        }
        else 0

        val nextMaintenance = schedules.mapNotNull { schedule ->
            val days = schedule.daysUntilDue(now)
            if (days != null) schedule.category.label to days else null
        }
            .minByOrNull { it.second }

        val costByCategory = CostCategory.entries
            .mapNotNull { cat ->
                val total = costs.filter { it.category == cat }
                    .sumOf { it.amount }
                if (total > 0) cat to total else null
            }
            .sortedByDescending { it.second }

        val monthlyCostTrend = (5 downTo 0).map { monthsAgo ->
            val month = now.minusMonths(monthsAgo.toLong())
            val total = costs
                .filter { it.date.year == month.year && it.date.month == month.month }
                .sumOf { it.amount }
            "${month.monthValue}月" to total.toFloat()
        }

        val monthlyCostByCategory = (5 downTo 0).map { monthsAgo ->
            val month = now.minusMonths(monthsAgo.toLong())
            val monthCosts = costs
                .filter { it.date.year == month.year && it.date.month == month.month }
            val byCategory = CostCategory.entries
                .mapNotNull { cat ->
                    val total = monthCosts.filter { it.category == cat }
                        .sumOf { it.amount }
                    if (total > 0) cat to total else null
                }
                .sortedByDescending { it.second }
            MonthlyCostSlice("${month.monthValue}月", byCategory)
        }

        val fuelEconomyTrend = fuels
            .filter { it.fuelEconomy != null }
            .take(20)
            .reversed()
            .map { r -> "${r.date.monthValue}/${r.date.dayOfMonth}" to r.fuelEconomy!!.toFloat() }

        return VehicleDashboard(
            monthlyCost = monthlyCost,
            yearlyCost = yearlyCost,
            totalCost = totalCost,
            averageMonthlyCost = CostStatisticsCalculator
                .averageMonthlyCost(costs, now),
            averageFuelEconomy = avgEconomy,
            totalDistance = totalDistance,
            costPerKm = CostStatisticsCalculator.costPerKm(totalCost, totalDistance),
            nextMaintenanceLabel = nextMaintenance?.first,
            nextMaintenanceDays = nextMaintenance?.second,
            costByCategory = costByCategory,
            monthlyCostTrend = monthlyCostTrend,
            fuelEconomyTrend = fuelEconomyTrend,
            monthlyCostByCategory = monthlyCostByCategory,
        )
    }
}
