package com.segnities007.stylish_myvehicles.presentation.screen.settings.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.model.ThemeMode
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import com.segnities007.stylishui.components.molecules.StylishConnectedCardColumn
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.components.organisms.StylishBottomSheet
import com.segnities007.stylishui.components.organisms.StylishDialogActions

/** テーマ選択のオプションを表示するボトムシート。選択は即時反映（完了/キャンセルは閉じるだけ）。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsOptionSheet(
    selectedThemeMode: ThemeMode,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StylishBottomSheet(
        onDismiss = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier,
    ) {
        Text(
            stringResource(R.string.theme_section),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp),
        )
        StylishConnectedCardColumn(
            items = ThemeMode.entries.map { mode ->
                val trailing: (@Composable () -> Unit)? =
                    if (mode == selectedThemeMode) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        null
                    }
                StylishConnectedCardItem(
                    title = mode.label,
                    onClick = { onThemeModeSelect(mode) },
                    trailingContent = trailing,
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(24.dp))
        StylishDialogActions(
            confirmLabel = stringResource(R.string.done),
            cancelLabel = stringResource(R.string.cancel),
            onConfirm = onDismiss,
            onCancel = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        )
    }
}

@Preview(name = "SettingsOptionSheet", showBackground = true, widthDp = 393, heightDp = 480)
@Composable
private fun SettingsOptionSheetPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            SettingsOptionSheet(
                selectedThemeMode = ThemeMode.SYSTEM,
                onThemeModeSelect = {},
                onDismiss = {},
            )
        }
    }
}
