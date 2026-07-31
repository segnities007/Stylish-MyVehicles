package com.segnities007.stylishui.components.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.tokens.StylishDimensions

@Composable
fun StylishIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    active: Boolean = false,
    containerColor: Color? = null,
    contentColor: Color? = null,
) {
    val resolvedContainerColor = containerColor ?: if (active) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val resolvedContentColor = contentColor ?: if (active) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
        shape = CircleShape,
        color = resolvedContainerColor,
        border = BorderStroke(
            StylishDimensions.outlineWidth,
            MaterialTheme.colorScheme.outlineVariant,
        ),
        shadowElevation = if (enabled) StylishDimensions.interactiveElevation else 0.dp,
    ) {
        IconButton(onClick = onClick, enabled = enabled) {
            Icon(imageVector, contentDescription, tint = resolvedContentColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StylishIconButtonPreview() {
    MaterialTheme {
        StylishIconButton(Icons.Default.Search, "検索", {})
    }
}
