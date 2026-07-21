package com.segnities007.stylish_myvehicles.presentation.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.utils.stylishConnectedGridCorners
import com.segnities007.stylish_myvehicles.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedButtonItem
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun StylishConnectedButtonGrid(
    items: List<StylishConnectedButtonItem>,
    columns: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = 2.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
    defaultColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ),
) {
    require(columns > 0) { "columns must be greater than zero" }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(spacing)) {
        items.chunked(columns)
            .forEachIndexed { rowIndex, rowItems ->
                Row(
                    Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                ) {
                    rowItems.forEachIndexed { columnIndex, item ->
                        val index = rowIndex * columns + columnIndex
                        Button(
                            onClick = item.onClick,
                            enabled = item.enabled,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .heightIn(min = 52.dp),
                            shape = stylishConnectedShape(
                                stylishConnectedGridCorners(index, items.size, columns),
                            ),
                            colors = item.colors ?: defaultColors,
                            contentPadding = contentPadding,
                        ) {
                            GridButtonSlot(item.leadingContent, Alignment.CenterStart)
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                content = item.content,
                            )
                            GridButtonSlot(item.trailingContent, Alignment.CenterEnd)
                        }
                    }
                    if (rowItems.size == columns) {
                        repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
    }
}

@Composable
private fun RowScope.GridButtonSlot(
    content: (@Composable RowScope.() -> Unit)?,
    alignment: Alignment,
) {
    Box(Modifier.widthIn(min = 24.dp), contentAlignment = alignment) {
        if (content != null) Row(content = content)
    }
}

@Preview(name = "Connected button grid", showBackground = true, widthDp = 393)
@Composable
private fun StylishConnectedButtonGridPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedButtonGrid(
                items = listOf(
                    StylishConnectedButtonItem({}, leadingContent = {
                        Icon(Icons.Default.Add, null)
                    }) { Text("追加\n\n\n\n\n\\") },
                    StylishConnectedButtonItem({}, leadingContent = {
                        Icon(Icons.Default.Edit, null)
                    }) { Text("編集") },
                    StylishConnectedButtonItem({}) { Text("その他") },
                ),
                columns = 2,
            )
        }
    }
}
