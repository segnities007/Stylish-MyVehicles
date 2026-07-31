package com.segnities007.stylishui.components.molecules

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
import com.segnities007.stylishui.components.models.StylishConnectedButtonItem
import com.segnities007.stylishui.foundation.ConnectedEdges
import com.segnities007.stylishui.foundation.connectedGridCorners
import com.segnities007.stylishui.foundation.connectedOutline
import com.segnities007.stylishui.foundation.connectedShape
import com.segnities007.stylishui.foundation.isActionable
import com.segnities007.stylishui.theme.StylishTheme
import com.segnities007.stylishui.theme.stylishComponentColors
import com.segnities007.stylishui.tokens.StylishDimensions

@Composable
fun StylishConnectedButtonGrid(
    items: List<StylishConnectedButtonItem>,
    columns: Int,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    spacing: Dp = StylishDimensions.connectedSpacing,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
    defaultColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.stylishComponentColors.groupedContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ),
) {
    require(columns > 0) { "columns must be greater than zero" }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items.chunked(columns)
            .forEachIndexed { rowIndex, rowItems ->
                Row(
                    Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                ) {
                    rowItems.forEachIndexed { columnIndex, item ->
                        val index = rowIndex * columns + columnIndex
                        val corners = connectedGridCorners(index, items.size, columns)
                        val actionable = isActionable(
                            enabled = item.enabled,
                            hasClickAction = item.onClick != null,
                        )
                        Button(
                            onClick = { item.onClick?.invoke() },
                            enabled = actionable,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .heightIn(min = 52.dp)
                                .connectedOutline(
                                    edges = ConnectedEdges.All,
                                    corners = corners,
                                    cornerRadius = cornerRadius,
                                ),
                            shape = connectedShape(
                                corners,
                                cornerRadius = cornerRadius,
                            ),
                            colors = item.colors ?: defaultColors,
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = StylishDimensions.interactiveElevation,
                                pressedElevation = 0.dp,
                                disabledElevation = 0.dp,
                            ),
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
    StylishTheme(darkTheme = false) {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedButtonGrid(
                items = listOf(
                    StylishConnectedButtonItem({}, leadingContent = {
                        Icon(Icons.Default.Add, null)
                    }) { Text("追加\n") },
                    StylishConnectedButtonItem({}, leadingContent = {
                        Icon(Icons.Default.Edit, null)
                    }) { Text("編集") },
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
