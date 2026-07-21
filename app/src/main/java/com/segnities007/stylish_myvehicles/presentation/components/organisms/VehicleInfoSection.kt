package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

/** 車両の基本情報（年式・ナンバー・排気量など）の一覧。 */
@Composable
fun VehicleInfoSection(
    vehicle: Vehicle,
    modifier: Modifier = Modifier,
) {
    StylishConnectedListItemColumn(
        modifier = modifier,
        items = buildList {
            vehicle.year?.let { add(StylishConnectedListItem("年式", "${it}年", {})) }
            vehicle.plateNumber.takeIf { it.isNotBlank() }
                ?.let { add(StylishConnectedListItem("ナンバー", it, {})) }
            vehicle.displacement?.let { add(StylishConnectedListItem("排気量", "${it}cc", {})) }
            vehicle.weight?.let { add(StylishConnectedListItem("車両重量", "${it}kg", {})) }
            vehicle.maxLoadKg?.let { add(StylishConnectedListItem("最大積載量", "${it}kg", {})) }
            vehicle.color.takeIf { it.isNotBlank() }
                ?.let { add(StylishConnectedListItem("カラー", it, {})) }
            vehicle.firstRegistrationDate?.let {
                add(
                    StylishConnectedListItem(
                        "初度登録",
                        it.toString(),
                        {})
                )
            }
            vehicle.insuranceCompany.takeIf { it.isNotBlank() }
                ?.let { add(StylishConnectedListItem("保険会社", it, {})) }
            vehicle.insuranceRank?.let { add(StylishConnectedListItem("等級", "${it}等級", {})) }
        },
    )
}

@Preview(name = "Vehicle info section", showBackground = true, widthDp = 393)
@Composable
private fun VehicleInfoSectionPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            VehicleInfoSection(
                vehicle = Vehicle(
                    maker = "トヨタ", name = "プリウス", grade = "Z",
                    year = 2022, plateNumber = "品川 330 あ 12-34",
                    displacement = 1800, weight = 1350, color = "ホワイトパール",
                    firstRegistrationDate = LocalDate.of(2022, 4, 1),
                    insuranceCompany = "東京海上日動", insuranceRank = 20,
                ),
            )
        }
    }
}
