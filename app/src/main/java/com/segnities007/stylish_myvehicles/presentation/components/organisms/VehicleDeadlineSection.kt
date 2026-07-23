package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.service.InspectionCalculator
import com.segnities007.stylish_myvehicles.domain.service.VehicleTaxCalculator
import com.segnities007.stylishui.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylishui.components.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

private const val NOT_REGISTERED = "未登録"

private fun deadlineStatus(expiry: LocalDate): String {
    val days = InspectionCalculator.daysUntilExpiry(expiry)
    return when {
        days < 0 -> "⚠️ 期限切れ"
        days <= 30 -> "⚠️ あと${days}日"
        days <= 365 -> "あと${days / 30}ヶ月"
        else -> "あと${days / 365}年${(days % 365) / 30}ヶ月"
    }
}

/**
 * 車検・自賠責・任意保険・自動車税の期限一覧。値が未登録でも全て表示し、
 * 期限項目のタップで [onEditField] 経由で編集ダイアログを開く。
 */
@Composable
fun VehicleDeadlineSection(
    vehicle: Vehicle,
    taxPaidThisYear: Boolean,
    onEditField: (VehicleField) -> Unit,
    onTaxClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StylishConnectedListItemColumn(
        modifier = modifier,
        items = buildList {
            val chevron: @Composable RowScope.() -> Unit = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            val inspectionExpiry = vehicle.currentInspectionExpiry
            add(
                StylishConnectedListItem(
                    "車検",
                    inspectionExpiry?.let { "$it（${deadlineStatus(it)}）" } ?: NOT_REGISTERED,
                    onClick = { onEditField(VehicleField.INSPECTION_EXPIRY) },
                    trailingContent = chevron,
                )
            )

            val jibaiExpiry = vehicle.jibaiExpiry
            add(
                StylishConnectedListItem(
                    "自賠責",
                    jibaiExpiry?.let { "$it（${deadlineStatus(it)}）" } ?: NOT_REGISTERED,
                    onClick = { onEditField(VehicleField.JIBAI_EXPIRY) },
                    trailingContent = chevron,
                )
            )

            val insuranceExpiry = vehicle.insuranceExpiry
            add(
                StylishConnectedListItem(
                    "任意保険",
                    insuranceExpiry?.let { "$it（${deadlineStatus(it)}）" } ?: NOT_REGISTERED,
                    onClick = { onEditField(VehicleField.INSURANCE_EXPIRY) },
                    trailingContent = chevron,
                )
            )

            val tax = VehicleTaxCalculator.calculateTax(
                vehicle.category,
                vehicle.displacement,
                vehicle.maxLoadKg
            )
            if (tax != null) {
                val taxStatus = if (taxPaidThisYear) " ✓ 納付済み" else " 未納付"
                add(
                    StylishConnectedListItem(
                        taxLabel(vehicle.category),
                        "${String.format("%,d", tax)}円/年$taxStatus",
                        onTaxClick,
                        trailingContent = chevron,
                    )
                )
            }
        },
    )
}

private fun taxLabel(category: VehicleCategory): String =
    when (category) {
        VehicleCategory.MOTORCYCLE -> "軽自動車税"
        else -> "自動車税"
    }

@Preview(name = "Vehicle deadline section", showBackground = true, widthDp = 393)
@Composable
private fun VehicleDeadlineSectionPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            VehicleDeadlineSection(
                vehicle = Vehicle(
                    maker = "トヨタ", name = "プリウス",
                    firstRegistrationDate = LocalDate.now()
                        .minusYears(2),
                    jibaiExpiry = LocalDate.now()
                        .plusDays(45),
                    insuranceExpiry = LocalDate.now()
                        .plusMonths(5),
                    displacement = 1800,
                ),
                taxPaidThisYear = true,
                onEditField = {},
                onTaxClick = {},
            )
        }
    }
}
