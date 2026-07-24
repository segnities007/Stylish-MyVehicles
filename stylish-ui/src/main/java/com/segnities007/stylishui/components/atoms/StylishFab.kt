package com.segnities007.stylishui.components.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.tokens.StylishDimensions

@Composable
fun StylishFab(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            StylishDimensions.outlineWidth,
            MaterialTheme.colorScheme.outlineVariant,
        ),
        tonalElevation = StylishDimensions.floatingElevation,
        shadowElevation = StylishDimensions.floatingElevation,
    ) {
        IconButton(onClick = onClick, enabled = enabled) {
            Icon(imageVector, contentDescription)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StylishFabPreview() {
    MaterialTheme {
        StylishFab(Icons.Default.Add, "追加", {})
    }
}
