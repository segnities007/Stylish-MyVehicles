package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem

/**
 * 「記録を追加」選択Dialog。給油/整備/費用のいずれかを選んで記録画面へ遷移する。
 * どの画面からでも開けるようAppNavigation直下で表示する。
 */
@Composable
fun AddRecordDialog(
    vehicleId: Long?,
    onDismiss: () -> Unit,
    onAddFuel: (Long) -> Unit,
    onAddMaintenance: (Long) -> Unit,
    onAddCost: (Long) -> Unit,
    onAddTrip: (Long) -> Unit,
) {
    StylishDialogSurface(onDismiss = onDismiss) {
        Column(Modifier.padding(24.dp)) {
            Text("記録を追加", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            StylishConnectedCardGrid(
                columns = 2,
                spacing = 4.dp,
                items = listOf(
                    StylishConnectedCardItem(
                        title = "給油",
                        onClick = {
                            onDismiss()
                            if (vehicleId != null) onAddFuel(vehicleId)
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.LocalGasStation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    ),
                    StylishConnectedCardItem(
                        title = "整備",
                        onClick = {
                            onDismiss()
                            if (vehicleId != null) onAddMaintenance(vehicleId)
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.Build,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    ),
                    StylishConnectedCardItem(
                        title = "費用",
                        onClick = {
                            onDismiss()
                            if (vehicleId != null) onAddCost(vehicleId)
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    ),
                    StylishConnectedCardItem(
                        title = "移動",
                        onClick = {
                            onDismiss()
                            if (vehicleId != null) onAddTrip(vehicleId)
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.Route,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    ),
                ),
            )
        }
    }
}
