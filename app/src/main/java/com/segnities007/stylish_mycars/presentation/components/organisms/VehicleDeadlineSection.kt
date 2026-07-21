package com.segnities007.stylish_mycars.presentation.components.organisms

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.domain.model.Vehicle
import com.segnities007.stylish_mycars.domain.service.InspectionCalculator
import com.segnities007.stylish_mycars.domain.service.VehicleTaxCalculator
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import java.time.LocalDate

/** 車検・自賠責・任意保険・自動車税の期限一覧。タップで編集へ誘導する。 */
@Composable
fun VehicleDeadlineSection(
    vehicle: Vehicle,
    onEdit: () -> Unit,
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
            vehicle.currentInspectionExpiry?.let { expiry ->
                val days = InspectionCalculator.daysUntilExpiry(expiry)
                val status = when {
                    days < 0 -> "⚠️ 期限切れ"
                    days <= 30 -> "⚠️ あと${days}日"
                    days <= 365 -> "あと${days / 30}ヶ月"
                    else -> "あと${days / 365}年${(days % 365) / 30}ヶ月"
                }
                add(StylishConnectedListItem("車検", "$expiry（$status）", onEdit, trailingContent = chevron))
            }
            vehicle.jibaiExpiry?.let { expiry ->
                val days = InspectionCalculator.daysUntilExpiry(expiry)
                val status = if (days < 0) "⚠️ 期限切れ" else "あと${days}日"
                add(StylishConnectedListItem("自賠責", "$expiry（$status）", onEdit, trailingContent = chevron))
            }
            vehicle.insuranceExpiry?.let { expiry ->
                val days = InspectionCalculator.daysUntilExpiry(expiry)
                val status = if (days < 0) "⚠️ 期限切れ" else "あと${days}日"
                add(StylishConnectedListItem("任意保険", "$expiry（$status）", onEdit, trailingContent = chevron))
            }
            val tax = VehicleTaxCalculator.calculateTax(vehicle.category, vehicle.displacement, vehicle.maxLoadKg)
            if (tax != null) {
                add(StylishConnectedListItem(
                    taxLabel(vehicle.category),
                    "${String.format("%,d", tax)}円/年${if (vehicle.taxPaid) " ✓" else ""}",
                    onEdit,
                    trailingContent = chevron,
                ))
            }
        },
    )
}

private fun taxLabel(category: com.segnities007.stylish_mycars.domain.model.VehicleCategory): String =
    when (category) {
        com.segnities007.stylish_mycars.domain.model.VehicleCategory.MOTORCYCLE -> "軽自動車税"
        else -> "自動車税"
    }

@Preview(name = "Vehicle deadline section", showBackground = true, widthDp = 393)
@Composable
private fun VehicleDeadlineSectionPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            VehicleDeadlineSection(
                vehicle = Vehicle(
                    maker = "トヨタ", name = "プリウス",
                    firstRegistrationDate = LocalDate.now().minusYears(2),
                    jibaiExpiry = LocalDate.now().plusDays(45),
                    insuranceExpiry = LocalDate.now().plusMonths(5),
                    displacement = 1800,
                    taxPaid = true,
                ),
                onEdit = {},
            )
        }
    }
}
