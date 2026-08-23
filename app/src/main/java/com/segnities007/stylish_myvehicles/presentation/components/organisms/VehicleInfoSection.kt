package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylishui.components.molecules.StylishConnectedCardColumn
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

private const val NOT_REGISTERED = "未登録"

private fun String?.orNotRegistered(): String =
    this?.takeIf { it.isNotBlank() } ?: NOT_REGISTERED

/**
 * 車両の基本情報一覧。値が未登録の項目も含めて全て表示し、
 * 項目タップで [onEditField] 経由で編集ダイアログを開く。
 */
@Composable
fun VehicleInfoSection(
    vehicle: Vehicle,
    onEditField: (VehicleField) -> Unit,
    modifier: Modifier = Modifier,
) {
    StylishConnectedCardColumn(
        modifier = modifier,
        items = listOf(
            StylishConnectedCardItem(
                VehicleField.CATEGORY.label,
                vehicle.category.label,
                onClick = { onEditField(VehicleField.CATEGORY) },
            ),
            StylishConnectedCardItem(
                VehicleField.MAKER.label,
                vehicle.maker.orNotRegistered(),
                onClick = { onEditField(VehicleField.MAKER) },
            ),
            StylishConnectedCardItem(
                VehicleField.NAME.label,
                vehicle.name.orNotRegistered(),
                onClick = { onEditField(VehicleField.NAME) },
            ),
            StylishConnectedCardItem(
                VehicleField.GRADE.label,
                vehicle.grade.orNotRegistered(),
                onClick = { onEditField(VehicleField.GRADE) },
            ),
            StylishConnectedCardItem(
                VehicleField.YEAR.label,
                vehicle.year?.let { "${it}年" } ?: NOT_REGISTERED,
                onClick = { onEditField(VehicleField.YEAR) },
            ),
            StylishConnectedCardItem(
                VehicleField.MODEL_CODE.label,
                vehicle.modelCode.orNotRegistered(),
                onClick = { onEditField(VehicleField.MODEL_CODE) },
            ),
            StylishConnectedCardItem(
                VehicleField.PLATE_NUMBER.label,
                vehicle.plateNumber.orNotRegistered(),
                onClick = { onEditField(VehicleField.PLATE_NUMBER) },
            ),
            StylishConnectedCardItem(
                VehicleField.VIN.label,
                vehicle.vin.orNotRegistered(),
                onClick = { onEditField(VehicleField.VIN) },
            ),
            StylishConnectedCardItem(
                VehicleField.DISPLACEMENT.label,
                vehicle.displacement?.let { "${it}cc" } ?: NOT_REGISTERED,
                onClick = { onEditField(VehicleField.DISPLACEMENT) },
            ),
            StylishConnectedCardItem(
                VehicleField.WEIGHT.label,
                vehicle.weight?.let { "${it}kg" } ?: NOT_REGISTERED,
                onClick = { onEditField(VehicleField.WEIGHT) },
            ),
            StylishConnectedCardItem(
                VehicleField.MAX_LOAD.label,
                vehicle.maxLoadKg?.let { "${it}kg" } ?: NOT_REGISTERED,
                onClick = { onEditField(VehicleField.MAX_LOAD) },
            ),
            StylishConnectedCardItem(
                VehicleField.COLOR.label,
                vehicle.color.orNotRegistered(),
                onClick = { onEditField(VehicleField.COLOR) },
            ),
            StylishConnectedCardItem(
                VehicleField.FIRST_REGISTRATION_DATE.label,
                vehicle.firstRegistrationDate?.toString() ?: NOT_REGISTERED,
                onClick = { onEditField(VehicleField.FIRST_REGISTRATION_DATE) },
            ),
            StylishConnectedCardItem(
                VehicleField.INSURANCE_COMPANY.label,
                vehicle.insuranceCompany.orNotRegistered(),
                onClick = { onEditField(VehicleField.INSURANCE_COMPANY) },
            ),
            StylishConnectedCardItem(
                VehicleField.INSURANCE_RANK.label,
                vehicle.insuranceRank?.let { "${it}等級" } ?: NOT_REGISTERED,
                onClick = { onEditField(VehicleField.INSURANCE_RANK) },
            ),
            StylishConnectedCardItem(
                VehicleField.MEMO.label,
                vehicle.memo.orNotRegistered(),
                onClick = { onEditField(VehicleField.MEMO) },
            ),
        ),
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
                onEditField = {},
            )
        }
    }
}
