package com.segnities007.stylish_myvehicles.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.segnities007.stylish_myvehicles.domain.usecase.ExportDocument
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishFab
import com.segnities007.stylish_myvehicles.presentation.components.organisms.LocalBottomBarVisible
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishBottomBar
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListScreen
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordScreen
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.licenses.LicensesScreen
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordScreen
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.notification.NotificationScreen
import com.segnities007.stylish_myvehicles.presentation.screen.notification.NotificationViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.onboarding.OnboardingScreen
import com.segnities007.stylish_myvehicles.presentation.screen.records.PeriodPreference
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsScreen
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordTopic
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.settings.SettingsScreen
import com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.VehicleDetailScreen
import com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.VehicleDetailViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit.VehicleEditScreen
import com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit.VehicleEditViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehiclePagerScreen
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehiclePagerViewModel
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import com.segnities007.stylish_myvehicles.presentation.theme.ThemeMode
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun AppNavigation(
    showOnboarding: Boolean,
    onSaveDocument: (ExportDocument) -> Unit,
    onThemeChanged: (ThemeMode) -> Unit,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = remember {
        mutableStateListOf<AppDestination>(
            if (showOnboarding) OnboardingDestination else VehiclePagerDestination,
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomBarVisible = remember { mutableStateOf(true) }
    val showAddDialog = remember { mutableStateOf(false) }

    fun popBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    // 下部バーのタブ遷移。ルート（ホーム）まで戻してから選択タブを積み、スタックの積み上がりを防ぐ。
    fun navigateToTab(tab: AppDestination) {
        while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        if (tab !is VehiclePagerDestination) {
            backStack.add(tab)
        }
    }

    val currentDestination = backStack.lastOrNull()
    val showBottomBar = (currentDestination is VehiclePagerDestination ||
            currentDestination is NotificationDestination) && bottomBarVisible.value

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp),
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
            ) {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    StylishBottomBar(
                        onNavigateToHome = { navigateToTab(VehiclePagerDestination) },
                        onNavigateToNotifications = { navigateToTab(NotificationDestination) },
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentDestination is VehiclePagerDestination && bottomBarVisible.value,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
            ) {
                StylishFab(
                    imageVector = Icons.Default.Add,
                    contentDescription = "記録を追加",
                    onClick = { showAddDialog.value = true },
                )
            }
        },
    ) { _ ->
        NavDisplay(
            backStack = backStack,
            transitionSpec = {
                (slideInHorizontally(tween(260)) { it / 5 } + fadeIn(tween(220))) togetherWith
                        (slideOutHorizontally(tween(260)) { -it / 10 } + fadeOut(tween(180)))
            },
            popTransitionSpec = {
                (slideInHorizontally(tween(260)) { -it / 5 } + fadeIn(tween(220))) togetherWith
                        (slideOutHorizontally(tween(260)) { it / 10 } + fadeOut(tween(180)))
            },
            predictivePopTransitionSpec = { _ ->
                (slideInHorizontally(tween(260)) { -it / 5 } + fadeIn(tween(220))) togetherWith
                        (slideOutHorizontally(tween(260)) { it / 10 } + fadeOut(tween(180)))
            },
            onBack = { popBack() },
            entryProvider = entryProvider {
                entry<OnboardingDestination> {
                    OnboardingScreen(
                        onComplete = {
                            onOnboardingComplete()
                            backStack.clear()
                            backStack.add(VehiclePagerDestination)
                        },
                    )
                }
                entry<VehiclePagerDestination> {
                    val viewModel: VehiclePagerViewModel = koinViewModel()
                    VehiclePagerScreen(
                        viewModel = viewModel,
                        onNavigateToEdit = { backStack.add(VehicleEditDestination(it)) },
                        onNavigateToSettings = { backStack.add(SettingsDestination) },
                        onNavigateToFuel = { backStack.add(RecordsDestination(it, RecordTopic.FUEL)) },
                        onNavigateToMaintenance = { backStack.add(RecordsDestination(it, RecordTopic.MAINTENANCE)) },
                        onNavigateToCost = { backStack.add(RecordsDestination(it, RecordTopic.COST)) },
                        onNavigateToVehicleDetail = { backStack.add(VehicleDetailDestination(it)) },
                        onNavigateToNotifications = { backStack.add(NotificationDestination) },
                        onAddFuel = { backStack.add(FuelRecordDestination(it, openAdd = true)) },
                        onAddMaintenance = { backStack.add(MaintenanceRecordDestination(it, openAdd = true)) },
                        onAddCost = { backStack.add(CostListDestination(it, openAdd = true)) },
                        bottomBarVisible = bottomBarVisible,
                        showAddDialog = showAddDialog,
                    )
                }
                entry<RecordsDestination> { dest ->
                    val context = LocalContext.current
                    val initialMode = remember { PeriodPreference.getMode(context) }
                    val viewModel: RecordsViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId, dest.topic, initialMode) },
                    )
                    RecordsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                        onNavigateToFuel = { vehicleId, recordId ->
                            backStack.add(FuelRecordDestination(vehicleId))
                        },
                        onNavigateToMaintenance = { vehicleId, recordId ->
                            backStack.add(MaintenanceRecordDestination(vehicleId))
                        },
                        onNavigateToCost = { vehicleId, recordId ->
                            backStack.add(CostListDestination(vehicleId))
                        },
                    )
                }
                entry<VehicleDetailDestination> { dest ->
                    val viewModel: VehicleDetailViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    VehicleDetailScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                        onNavigateToEdit = { popBack(); backStack.add(VehicleEditDestination(it)) },
                        onNavigateToFuel = { backStack.add(RecordsDestination(it, RecordTopic.FUEL)) },
                        onNavigateToMaintenance = { backStack.add(RecordsDestination(it, RecordTopic.MAINTENANCE)) },
                        onNavigateToCost = { backStack.add(RecordsDestination(it, RecordTopic.COST)) },
                        onSaveDocument = onSaveDocument,
                    )
                }
                entry<VehicleEditDestination> { dest ->
                    val viewModel: VehicleEditViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    VehicleEditScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                    )
                }
                entry<FuelRecordDestination> { dest ->
                    val viewModel: FuelRecordViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    FuelRecordScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                        openAddDialog = dest.openAdd,
                    )
                }
                entry<MaintenanceRecordDestination> { dest ->
                    val viewModel: MaintenanceRecordViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    MaintenanceRecordScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                        openAddDialog = dest.openAdd,
                    )
                }
                entry<CostListDestination> { dest ->
                    val viewModel: CostListViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    CostListScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                        openAddDialog = dest.openAdd,
                    )
                }
                entry<SettingsDestination> {
                    SettingsScreen(
                        onNavigateBack = { popBack() },
                        onThemeChanged = onThemeChanged,
                        onNavigateToLicenses = { backStack.add(LicensesDestination) },
                    )
                }
                entry<LicensesDestination> {
                    LicensesScreen(
                        onNavigateBack = { popBack() },
                    )
                }
                entry<NotificationDestination> {
                    val viewModel: NotificationViewModel = koinViewModel()
                    NotificationScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                        onAddRecord = { /* TODO: show add record dialog */ },
                        onNavigateToHome = { popBack() },
                    )
                }
            },
        )
    }
}

@Preview(name = "AppNavigation", showBackground = true, widthDp = 393)
@Composable
private fun AppNavigationPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            AppNavigation(
                showOnboarding = false,
                onSaveDocument = {},
                onThemeChanged = {},
                onOnboardingComplete = {},
            )
        }
    }
}
