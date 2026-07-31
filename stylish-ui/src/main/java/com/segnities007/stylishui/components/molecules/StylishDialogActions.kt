package com.segnities007.stylishui.components.molecules

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.models.StylishConnectedButtonItem
import com.segnities007.stylishui.theme.StylishTheme

@Composable
fun StylishDialogActions(
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    confirmEnabled: Boolean = true,
) {
    StylishConnectedButtonRow(
        items = listOf(
            StylishConnectedButtonItem(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) { Text(cancelLabel) },
            StylishConnectedButtonItem(
                onClick = onConfirm,
                enabled = confirmEnabled,
            ) { Text(confirmLabel) },
        ),
        modifier = Modifier.fillMaxWidth(),
        defaultColors = ButtonDefaults.buttonColors(),
    )
}

@Preview(name = "Stylish dialog actions", showBackground = true, widthDp = 393)
@Composable
private fun StylishDialogActionsPreview() {
    StylishTheme(darkTheme = false) {
        Card(Modifier.padding(20.dp)) {
            StylishDialogActions("", "キャンセル", {}, {})
        }
    }
}
