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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylishui.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylishui.components.models.StylishConnectedListItem
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import com.segnities007.stylish_myvehicles.presentation.theme.ThemeMode
import com.segnities007.stylish_myvehicles.presentation.theme.ThemePreference

@Composable
fun SettingsScreen(
    onThemeChanged: (ThemeMode) -> Unit,
    onNavigateToLicenses: () -> Unit,
    bottomBarVisible: MutableState<Boolean>? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var currentTheme by remember { mutableStateOf(ThemePreference.getThemeMode(context)) }
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
                StylishConnectedListItemColumn(
                    items = ThemeMode.entries.map { mode ->
                        StylishConnectedListItem(
                            headline = mode.label,
                            supportingText = if (mode == currentTheme) stringResource(R.string.theme_selected) else null,
                            onClick = {
                                currentTheme = mode
                                ThemePreference.setThemeMode(context, mode)
                                onThemeChanged(mode)
                            },
                        )
                    },
                )

                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(R.string.app_info_section),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                StylishConnectedListItemColumn(
                    items = listOf(
                        StylishConnectedListItem(stringResource(R.string.app_name_label), "Stylish MyCars"),
                        StylishConnectedListItem(stringResource(R.string.version_label), "1.0"),
                        StylishConnectedListItem(
                            headline = stringResource(R.string.open_source_licenses),
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
}

@Preview(name = "SettingsScreen", showBackground = true, widthDp = 393)
@Composable
private fun SettingsScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            SettingsScreen(onThemeChanged = {}, onNavigateToLicenses = {})
        }
    }
}
