package com.segnities007.stylish_myvehicles.presentation.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.ThemeMode
import com.segnities007.stylish_myvehicles.domain.repository.SettingsRepository
import com.segnities007.stylish_myvehicles.presentation.screen.settings.components.SettingsOptionSheet
import com.segnities007.stylishui.components.molecules.StylishConnectedCardColumn
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToLicenses: () -> Unit,
    bottomBarVisible: MutableState<Boolean>? = null,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val isAtTop by remember(scrollState) {
        derivedStateOf { scrollState.value == 0 }
    }
    LaunchedEffect(isAtTop) {
        bottomBarVisible?.value = isAtTop
    }

    StylishScaffold(
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text(stringResource(R.string.settings)) },
            )

            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    stringResource(R.string.theme_section),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                StylishConnectedCardColumn(
                    items = listOf(
                        StylishConnectedCardItem(
                            title = stringResource(R.string.theme_section),
                            supportingText = state.themeMode.label,
                            onClick = { viewModel.accept(SettingsIntent.OpenThemeSelector) },
                        ),
                    ),
                )

                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(R.string.app_info_section),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                StylishConnectedCardColumn(
                    items = listOf(
                        StylishConnectedCardItem(stringResource(R.string.app_name_label), "Stylish MyCars"),
                        StylishConnectedCardItem(stringResource(R.string.version_label), "1.0"),
                        StylishConnectedCardItem(
                            title = stringResource(R.string.open_source_licenses),
                            onClick = onNavigateToLicenses,
                            trailingContent = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward, null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                        ),
                    ),
                )
            }
        }
    }

    if (state.isThemeSheetVisible) {
        SettingsOptionSheet(
            selectedThemeMode = state.themeMode,
            onThemeModeSelect = { mode ->
                viewModel.accept(SettingsIntent.ThemeModeSelected(mode))
            },
            onDismiss = { viewModel.accept(SettingsIntent.CloseThemeSelector) },
        )
    }
}

@Preview(name = "SettingsScreen", showBackground = true, widthDp = 393)
@Composable
private fun SettingsScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            val settingsRepository = remember {
                object : SettingsRepository {
                    override fun getThemeMode() = flowOf(ThemeMode.SYSTEM)
                    override suspend fun setThemeMode(mode: ThemeMode) {}
                }
            }
            SettingsScreen(
                viewModel = remember { SettingsViewModel(settingsRepository) },
                onNavigateToLicenses = {},
            )
        }
    }
}
