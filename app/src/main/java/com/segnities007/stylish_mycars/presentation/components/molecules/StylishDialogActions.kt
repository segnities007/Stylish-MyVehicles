package com.segnities007.stylish_mycars.presentation.components.molecules

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
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedButtonItem
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

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
    StylishMyCarsTheme {
        Card(Modifier.padding(20.dp)) {
            StylishDialogActions("保存", "キャンセル", {}, {})
        }
    }
}
