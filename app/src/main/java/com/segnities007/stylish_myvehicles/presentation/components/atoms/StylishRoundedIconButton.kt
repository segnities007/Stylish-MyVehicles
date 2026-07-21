package com.segnities007.stylish_myvehicles.presentation.components.atoms

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun StylishRoundedIconButton(
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
    }
    else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val resolvedContentColor = contentColor ?: if (active) {
        MaterialTheme.colorScheme.onPrimary
    }
    else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        modifier = modifier.sizeIn(minWidth = 80.dp, minHeight = 48.dp),
        shape = RoundedCornerShape(24.dp),
        color = resolvedContainerColor,
    ) {
        IconButton(onClick = onClick, enabled = enabled) {
            Icon(imageVector, contentDescription, tint = resolvedContentColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StylishRoundedIconButtonPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(16.dp)) {
            StylishRoundedIconButton(Icons.Default.Add, "Add", {})
        }
    }
}
