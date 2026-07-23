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
import com.segnities007.stylishui.components.atoms.StylishFab
import com.segnities007.stylish_myvehicles.presentation.components.organisms.LocalBottomBarVisible
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishBottomBar
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.licenses.LicensesScreen
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.notification.NotificationScreen
import com.segnities007.stylish_myvehicles.presentation.screen.notification.NotificationViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.onboarding.OnboardingScreen
import com.segnities007.stylish_myvehicles.presentation.screen.records.CostRecordsScreen
import com.segnities007.stylish_myvehicles.presentation.screen.records.FuelRecordsScreen
import com.segnities007.stylish_myvehicles.presentation.screen.records.MaintenanceRecordsScreen
import com.segnities007.stylish_myvehicles.presentation.screen.records.PeriodPreference
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordTopic
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.recordslist.RecordsListScreen
import com.segnities007.stylish_myvehicles.presentation.screen.recordslist.RecordsListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.settings.SettingsScreen
import com.segnities007.stylish_myvehicles.presentation.screen.trip.TripRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.trip.TripRecordsScreen
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
            currentDestination is RecordsListDestination ||
            currentDestination is NotificationDestination ||
            currentDestination is SettingsDestination) && bottomBarVisible.value

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
                        onNavigateToSettings = { backStack.add(SettingsDestination) },
                        onNavigateToRecordsList = { navigateToTab(RecordsListDestination) },
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
                        onNavigateToFuel = { backStack.add(FuelRecordsDestination(it)) },
                        onNavigateToMaintenance = { backStack.add(MaintenanceRecordsDestination(it)) },
                        onNavigateToCost = { backStack.add(CostRecordsDestination(it)) },
                        onNavigateToTrip = { backStack.add(TripRecordsDestination(it)) },
                        onNavigateToVehicleDetail = { backStack.add(VehicleDetailDestination(it)) },
                        onNavigateToNotifications = { backStack.add(NotificationDestination) },
                        onAddFuel = {
                            backStack.add(FuelRecordsDestination(it, openAdd = true))
                        },
                        onAddMaintenance = {
                            backStack.add(MaintenanceRecordsDestination(it, openAdd = true))
                        },
                        onAddCost = {
                            backStack.add(CostRecordsDestination(it, openAdd = true))
                        },
                        onAddTrip = { backStack.add(TripRecordsDestination(it)) },
                        bottomBarVisible = bottomBarVisible,
                        showAddDialog = showAddDialog,
                    )
                }
                // Nav3のNavDisplayはエントリーごとのViewModelStoreを持たないため、
                // keyを明示してトピック/車両ごとに独立したViewModelインスタンスを確保する
                entry<FuelRecordsDestination> { dest ->
                    val context = LocalContext.current
                    val initialMode = remember { PeriodPreference.getMode(context) }
                    val viewModel: RecordsViewModel = koinViewModel(
                        key = "fuelRecords-${dest.vehicleId}",
                        parameters = { parametersOf(dest.vehicleId, RecordTopic.FUEL, initialMode) },
                    )
                    val dialogViewModel: FuelRecordViewModel = koinViewModel(
                        key = "fuelDialog-${dest.vehicleId}",
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    FuelRecordsScreen(
                        viewModel = viewModel,
                        dialogViewModel = dialogViewModel,
                        onNavigateBack = { popBack() },
                        openAddDialog = dest.openAdd,
                    )
                }
                entry<MaintenanceRecordsDestination> { dest ->
                    val context = LocalContext.current
                    val initialMode = remember { PeriodPreference.getMode(context) }
                    val viewModel: RecordsViewModel = koinViewModel(
                        key = "maintenanceRecords-${dest.vehicleId}",
                        parameters = {
                            parametersOf(dest.vehicleId, RecordTopic.MAINTENANCE, initialMode)
                        },
                    )
                    val dialogViewModel: MaintenanceRecordViewModel = koinViewModel(
                        key = "maintenanceDialog-${dest.vehicleId}",
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    MaintenanceRecordsScreen(
                        viewModel = viewModel,
                        dialogViewModel = dialogViewModel,
                        onNavigateBack = { popBack() },
                        openAddDialog = dest.openAdd,
                    )
                }
                entry<CostRecordsDestination> { dest ->
                    val context = LocalContext.current
                    val initialMode = remember { PeriodPreference.getMode(context) }
                    val viewModel: RecordsViewModel = koinViewModel(
                        key = "costRecords-${dest.vehicleId}",
                        parameters = { parametersOf(dest.vehicleId, RecordTopic.COST, initialMode) },
                    )
                    val dialogViewModel: CostListViewModel = koinViewModel(
                        key = "costDialog-${dest.vehicleId}",
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    CostRecordsScreen(
                        viewModel = viewModel,
                        dialogViewModel = dialogViewModel,
                        onNavigateBack = { popBack() },
                        openAddDialog = dest.openAdd,
                    )
                }
                entry<TripRecordsDestination> { dest ->
                    val viewModel: TripRecordViewModel = koinViewModel(
                        key = "tripRecords-${dest.vehicleId}",
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    TripRecordsScreen(
                        vehicleId = dest.vehicleId,
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
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
                        onNavigateToCost = { backStack.add(CostRecordsDestination(it)) },
                        onSaveDocument = onSaveDocument,
                    )
                }
                entry<VehicleEditDestination> { dest ->
                    val viewModel: VehicleEditViewModel = koinViewModel(
                        key = "vehicleEdit-${dest.sessionId}",
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    VehicleEditScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                    )
                }
                entry<RecordsListDestination> {
                    val viewModel: RecordsListViewModel = koinViewModel()
                    RecordsListScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                        onNavigateToFuel = { backStack.add(FuelRecordsDestination(it)) },
                        onNavigateToMaintenance = { backStack.add(MaintenanceRecordsDestination(it)) },
                        onNavigateToCost = { backStack.add(CostRecordsDestination(it)) },
                        onNavigateToTrip = { backStack.add(TripRecordsDestination(it)) },
                    )
                }
                entry<SettingsDestination> {
                    SettingsScreen(
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
