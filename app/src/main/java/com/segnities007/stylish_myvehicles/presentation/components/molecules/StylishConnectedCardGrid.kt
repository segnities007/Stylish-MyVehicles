package com.segnities007.stylish_myvehicles.presentation.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.utils.stylishConnectedGridCorners
import com.segnities007.stylish_myvehicles.presentation.components.atoms.utils.StylishConnectedEdges
import com.segnities007.stylish_myvehicles.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun StylishConnectedCardGrid(
    items: List<StylishConnectedCardItem>,
    columns: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = 4.dp,
) {
    require(columns > 0) { "columns must be greater than zero" }
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items.chunked(columns)
            .forEachIndexed { rowIndex, rowItems ->
                Row(
                    Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                ) {
                    val isFullRow = rowItems.size == columns
                    rowItems.forEachIndexed { columnIndex, item ->
                        val index = rowIndex * columns + columnIndex
                        val corners = stylishConnectedGridCorners(index, items.size, columns)
                        val isLastRow = rowIndex == (items.size - 1) / columns
                        StylishConnectedCard(
                            title = item.title,
                            supportingText = item.supportingText,
                            onClick = item.onClick,
                            onLongClick = item.onLongClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = stylishConnectedShape(
                                corners,
                            ),
                            outlineEdges = StylishConnectedEdges(
                                top = rowIndex == 0,
                                end = columnIndex == rowItems.lastIndex,
                                bottom = isLastRow,
                                start = columnIndex == 0,
                            ),
                            outlineCorners = corners,
                            trailingContent = item.trailingContent,
                        )
                    }
                    if (isFullRow) {
                        repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
    }
}

@Preview(name = "Connected card grid", showBackground = true, widthDp = 393)
@Composable
private fun StylishConnectedCardGridPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedCardGrid(
                items = List(5) { index ->
                    StylishConnectedCardItem("項目 ${index + 1}", "補足情報")
                },
                columns = 2,
            )
        }
    }
}
