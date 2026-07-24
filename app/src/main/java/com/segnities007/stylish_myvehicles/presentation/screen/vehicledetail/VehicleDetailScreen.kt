package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.usecase.ExportDocument
import com.segnities007.stylishui.components.atoms.StylishFab
import com.segnities007.stylishui.components.atoms.StylishIconButton
import com.segnities007.stylishui.components.molecules.StylishConnectedButtonColumn
import com.segnities007.stylishui.components.molecules.StylishDialogSurface
import com.segnities007.stylishui.components.models.StylishConnectedButtonItem
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishPageContent
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylishui.components.patterns.StylishSectionTitle
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleDeadlineSection
import com.segnities007.stylish_myvehicles.presentation.components.organisms.VehicleInfoSection
import com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.components.VehicleFieldEditDialog
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

/**
 * 車両情報画面。車両についての情報（期限管理・車両情報）のみを表示する。
 * FABからデータエクスポートを選択できる。
 */
@Composable
fun VehicleDetailScreen(
    viewModel: VehicleDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToCost: (Long) -> Unit,
    onSaveDocument: (ExportDocument) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showExportDialog by remember { mutableStateOf(false) }

    // ホーム画面と同じスクロールUI。最上部にいる間だけFABを表示する
    val listState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset <= 0
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehicleDetailEffect.NavigateBack -> onNavigateBack()
                is VehicleDetailEffect.NavigateToEdit -> onNavigateToEdit(effect.vehicleId)
                is VehicleDetailEffect.NavigateToCost -> onNavigateToCost(effect.vehicleId)
                is VehicleDetailEffect.SaveDocument -> onSaveDocument(effect.document)
            }
        }
    }

    StylishScaffold(
        modifier = modifier,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isAtTop,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
            ) {
                StylishFab(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = stringResource(R.string.data_export),
                    onClick = { showExportDialog = true },
                )
            }
        },
    ) {
        val vehicle = state.vehicle
        StylishPageContent(
            listState = listState,
            header = {
                StylishHeader(
                    title = {
                        Text(if (vehicle != null) "${vehicle.maker} ${vehicle.name}" else stringResource(R.string.vehicle_detail))
                    },
                    navigation = {
                        StylishIconButton(
                            Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back),
                            onClick = { viewModel.accept(VehicleDetailIntent.NavigateBack) },
                        )
                    },
                    actions = {
                        StylishIconButton(
                            Icons.Default.Edit, stringResource(R.string.edit),
                            onClick = { viewModel.accept(VehicleDetailIntent.EditVehicle) },
                        )
                    },
                )
            },
        ) {
            if (vehicle != null) {
                // 期限管理
                item {
                    StylishSectionTitle(stringResource(R.string.deadline_management))
                    VehicleDeadlineSection(
                        vehicle = vehicle,
                        taxPaidThisYear = state.taxPaidThisYear,
                        onEditField = {
                            viewModel.accept(VehicleDetailIntent.OpenFieldEditor(it))
                        },
                        onTaxClick = { viewModel.accept(VehicleDetailIntent.OpenCostList) },
                    )
                }

                // 車両情報
                item {
                    StylishSectionTitle(stringResource(R.string.vehicle_info))
                    VehicleInfoSection(
                        vehicle = vehicle,
                        onEditField = {
                            viewModel.accept(VehicleDetailIntent.OpenFieldEditor(it))
                        },
                    )
                    // FABの裏にコンテンツが隠れないよう余白を確保する
                    Spacer(Modifier.height(96.dp))
                }
            }
        }
    }

    state.editingField?.let { field ->
        VehicleFieldEditDialog(
            field = field,
            inputText = state.fieldInputText,
            inputDate = state.fieldInputDate,
            inputCategory = state.fieldInputCategory,
            onTextChanged = { viewModel.accept(VehicleDetailIntent.FieldTextChanged(it)) },
            onDateChanged = { viewModel.accept(VehicleDetailIntent.FieldDateChanged(it)) },
            onCategoryChanged = { viewModel.accept(VehicleDetailIntent.FieldCategoryChanged(it)) },
            onSave = { viewModel.accept(VehicleDetailIntent.SaveField) },
            onDismiss = { viewModel.accept(VehicleDetailIntent.CloseFieldEditor) },
        )
    }

    if (showExportDialog) {
        StylishDialogSurface(onDismiss = { showExportDialog = false }) {
            Column(Modifier.padding(24.dp)) {
                Text(stringResource(R.string.data_export), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                StylishConnectedButtonColumn(
                    items = listOf(
                        StylishConnectedButtonItem(
                            onClick = {
                                showExportDialog = false
                                viewModel.accept(VehicleDetailIntent.ExportFuelCsv)
                            },
                        ) { Text(stringResource(R.string.export_fuel_csv)) },
                        StylishConnectedButtonItem(
                            onClick = {
                                showExportDialog = false
                                viewModel.accept(VehicleDetailIntent.ExportMaintenanceCsv)
                            },
                        ) { Text(stringResource(R.string.export_maintenance_csv)) },
                        StylishConnectedButtonItem(
                            onClick = {
                                showExportDialog = false
                                viewModel.accept(VehicleDetailIntent.ExportCostCsv)
                            },
                        ) { Text(stringResource(R.string.export_cost_csv)) },
                    ),
                )
            }
        }
    }
}

@Preview(name = "VehicleDetailScreen", showBackground = true, widthDp = 393)
@Composable
private fun VehicleDetailScreenPreview() {
    val vehicle = Vehicle(
        id = 1L,
        category = VehicleCategory.CAR,
        maker = "トヨタ",
        name = "カローラ",
        grade = "G",
        year = 2020,
        plateNumber = "横浜 300 あ 12-34",
        firstRegistrationDate = java.time.LocalDate.of(2020, 4, 1),
        inspectionExpiry = java.time.LocalDate.of(2026, 4, 30),
        insuranceExpiry = java.time.LocalDate.of(2026, 10, 1),
        insuranceCompany = "東京海上日動",
        memo = "セダン / シルバー",
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            Column(Modifier.fillMaxSize()) {
                VehicleInfoSection(vehicle = vehicle, onEditField = {})
            }
        }
    }
}
