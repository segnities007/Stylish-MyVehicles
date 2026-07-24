package com.segnities007.stylish_myvehicles.presentation.screen.vehiclelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.Vehicle
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import com.segnities007.stylish_myvehicles.domain.service.InspectionCalculator
import com.segnities007.stylishui.components.atoms.StylishIconButton
import com.segnities007.stylishui.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylishui.components.models.StylishConnectedListItem
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun VehicleListScreen(
    viewModel: VehicleListViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToEdit: (Long?) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VehicleListEffect.NavigateToDetail ->
                    onNavigateToDetail(effect.vehicleId)

                is VehicleListEffect.NavigateToEdit ->
                    onNavigateToEdit(effect.vehicleId)
            }
        }
    }

    StylishScaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.accept(VehicleListIntent.AddVehicle) },
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface,
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_vehicle))
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text(stringResource(R.string.my_cars)) },
                actions = {
                    StylishIconButton(
                        Icons.Default.Settings, stringResource(R.string.settings),
                        onClick = onNavigateToSettings,
                    )
                },
            )

            // 検索バー（車両が2台以上で表示）
            if (state.vehicles.size >= 2) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.accept(VehicleListIntent.SearchQueryChanged(it)) },
                    placeholder = { Text(stringResource(R.string.search_by_maker_model_plate)) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )
            }

            when {
                state.isLoading -> LoadingState()
                state.vehicles.isEmpty() -> EmptyState(
                    onAddClick = { viewModel.accept(VehicleListIntent.AddVehicle) },
                )

                state.filteredVehicles.isEmpty() -> NoSearchResultState()
                else -> VehicleList(
                    vehicles = state.filteredVehicles,
                    onVehicleClick = { viewModel.accept(VehicleListIntent.SelectVehicle(it.id)) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun VehicleList(
    vehicles: List<Vehicle>,
    onVehicleClick: (Vehicle) -> Unit,
    modifier: Modifier = Modifier,
) {
    StylishConnectedListItemColumn(
        modifier = modifier,
        spacing = 4.dp,
        items = vehicles.map { vehicle ->
            StylishConnectedListItem(
                headline = "${vehicle.maker} ${vehicle.name}",
                supportingLines = buildSupportingLines(vehicle),
                onClick = { onVehicleClick(vehicle) },
            )
        },
    )
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                stringResource(R.string.no_vehicles_registered),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                stringResource(R.string.register_first_car_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            TextButton(
                onClick = onAddClick,
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text(stringResource(R.string.register_vehicle))
            }
        }
    }
}

@Composable
private fun NoSearchResultState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            stringResource(R.string.no_matching_vehicles),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun buildSupportingLines(vehicle: Vehicle): List<String> {
    val parts = mutableListOf<String>()
    vehicle.plateNumber.takeIf { it.isNotBlank() }
        ?.let { parts.add(it) }
    InspectionCalculator.currentExpiryFor(vehicle)?.let { expiry ->
        val days = InspectionCalculator.daysUntilExpiry(expiry)
        val label = when {
            days < 0 -> "⚠️ 車検切れ"
            days <= 30 -> "⚠️ 車検: あと${days}日"
            days <= 365 -> "車検: あと${days / 30}ヶ月"
            else -> "車検: ${expiry}"
        }
        parts.add(label)
    }
    return if (parts.isEmpty()) listOf("詳細を登録") else parts.toList()
}

@Preview(name = "VehicleListScreen", showBackground = true, widthDp = 393)
@Composable
private fun VehicleListScreenPreview() {
    val vehicles = listOf(
        Vehicle(
            id = 1L,
            maker = "トヨタ",
            name = "カローラ",
            category = VehicleCategory.CAR,
            grade = "G",
            year = 2020,
            plateNumber = "横浜 300 あ 12-34"
        ),
        Vehicle(
            id = 2L,
            maker = "ホンダ",
            name = "フィット",
            category = VehicleCategory.CAR,
            grade = "RS",
            year = 2021,
            plateNumber = "品川 500 い 56-78"
        ),
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            VehicleList(vehicles = vehicles, onVehicleClick = {})
        }
    }
}

@Preview(name = "VehicleList", showBackground = true, widthDp = 393)
@Composable
private fun VehicleListPreview() {
    val vehicles = listOf(
        Vehicle(
            id = 1L,
            maker = "トヨタ",
            name = "カローラ",
            category = VehicleCategory.CAR,
            grade = "G",
            year = 2020,
            plateNumber = "横浜 300 あ 12-34"
        ),
        Vehicle(
            id = 2L,
            maker = "ホンダ",
            name = "フィット",
            category = VehicleCategory.CAR,
            grade = "RS",
            year = 2021,
            plateNumber = "品川 500 い 56-78"
        ),
    )
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            VehicleList(vehicles = vehicles, onVehicleClick = {})
        }
    }
}

@Preview(name = "LoadingState", showBackground = true, widthDp = 393)
@Composable
private fun LoadingStatePreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            LoadingState()
        }
    }
}

@Preview(name = "EmptyState", showBackground = true, widthDp = 393)
@Composable
private fun EmptyStatePreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            EmptyState(onAddClick = {})
        }
    }
}

@Preview(name = "NoSearchResultState", showBackground = true, widthDp = 393)
@Composable
private fun NoSearchResultStatePreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            NoSearchResultState()
        }
    }
}
