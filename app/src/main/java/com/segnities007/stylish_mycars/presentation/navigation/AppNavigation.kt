package com.segnities007.stylish_mycars.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.segnities007.stylish_mycars.domain.usecase.ExportDocument
import com.segnities007.stylish_mycars.presentation.screen.cost.CostListScreen
import com.segnities007.stylish_mycars.presentation.screen.cost.CostListViewModel
import com.segnities007.stylish_mycars.presentation.screen.fuel.FuelRecordScreen
import com.segnities007.stylish_mycars.presentation.screen.fuel.FuelRecordViewModel
import com.segnities007.stylish_mycars.presentation.screen.licenses.LicensesScreen
import com.segnities007.stylish_mycars.presentation.screen.maintenance.MaintenanceRecordScreen
import com.segnities007.stylish_mycars.presentation.screen.maintenance.MaintenanceRecordViewModel
import com.segnities007.stylish_mycars.presentation.screen.onboarding.OnboardingScreen
import com.segnities007.stylish_mycars.presentation.screen.settings.SettingsScreen
import com.segnities007.stylish_mycars.presentation.screen.vehicleedit.VehicleEditScreen
import com.segnities007.stylish_mycars.presentation.screen.vehicleedit.VehicleEditViewModel
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.VehiclePagerScreen
import com.segnities007.stylish_mycars.presentation.screen.vehiclepager.VehiclePagerViewModel
import com.segnities007.stylish_mycars.presentation.theme.ThemeMode
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

    fun popBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        onNavigateToFuel = { backStack.add(FuelRecordDestination(it)) },
                        onNavigateToMaintenance = { backStack.add(MaintenanceRecordDestination(it)) },
                        onNavigateToCost = { backStack.add(CostListDestination(it)) },
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
                    )
                }
                entry<MaintenanceRecordDestination> { dest ->
                    val viewModel: MaintenanceRecordViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    MaintenanceRecordScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
                    )
                }
                entry<CostListDestination> { dest ->
                    val viewModel: CostListViewModel = koinViewModel(
                        parameters = { parametersOf(dest.vehicleId) },
                    )
                    CostListScreen(
                        viewModel = viewModel,
                        onNavigateBack = { popBack() },
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
            },
        )
    }
}
