package com.segnities007.stylish_myvehicles.presentation.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import com.segnities007.stylish_myvehicles.presentation.theme.ThemeMode
import com.segnities007.stylish_myvehicles.presentation.theme.ThemePreference

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onThemeChanged: (ThemeMode) -> Unit,
    onNavigateToLicenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var currentTheme by remember { mutableStateOf(ThemePreference.getThemeMode(context)) }

    StylishScaffold(
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text("設定") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                        onClick = onNavigateBack,
                    )
                },
            )

            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "テーマ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                StylishConnectedListItemColumn(
                    items = ThemeMode.entries.map { mode ->
                        StylishConnectedListItem(
                            headline = mode.label,
                            supportingText = if (mode == currentTheme) "✓ 選択中" else null,
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
                    "アプリ情報",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                StylishConnectedListItemColumn(
                    items = listOf(
                        StylishConnectedListItem("アプリ名", "Stylish MyCars", {}),
                        StylishConnectedListItem("バージョン", "1.0", {}),
                        StylishConnectedListItem(
                            headline = "オープンソースライセンス",
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
            SettingsScreen(onNavigateBack = {}, onThemeChanged = {}, onNavigateToLicenses = {})
        }
    }
}
