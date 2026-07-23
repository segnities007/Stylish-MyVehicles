package com.segnities007.stylishui.components.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.theme.StylishTheme

/** 削除操作の確認ダイアログ。破壊的操作の前に必ず挟む。 */
@Composable
fun StylishDeleteConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    StylishDialogSurface(onDismiss = onDismiss) {
        Column(Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = confirmLabel,
                cancelLabel = cancelLabel,
                onConfirm = onConfirm,
                onCancel = onDismiss,
            )
        }
    }
}

@Preview(name = "Delete confirm dialog", showBackground = true, widthDp = 393, heightDp = 400)
@Composable
private fun StylishDeleteConfirmDialogPreview() {
    StylishTheme(darkTheme = false) {
        StylishDeleteConfirmDialog(
            title = "給油記録を削除",
            message = "この給油記録を削除しますか？この操作は取り消せません。",
            confirmLabel = "削除",
            cancelLabel = "キャンセル",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
