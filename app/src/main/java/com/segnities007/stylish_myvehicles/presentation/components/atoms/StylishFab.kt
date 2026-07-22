package com.segnities007.stylish_myvehicles.presentation.components.atoms

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
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

/**
 * 下部ナビゲーションバー（StylishBottomBar）のUIに寄せたFAB。
 * バーと同じ surfaceContainerHigh・ボーダー・elevation を円形にした追加ボタン。
 */
@Composable
fun StylishFab(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector, contentDescription)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StylishFabPreview() {
    StylishMyVehiclesTheme {
        StylishFab(Icons.Default.Add, "追加", {})
    }
}
