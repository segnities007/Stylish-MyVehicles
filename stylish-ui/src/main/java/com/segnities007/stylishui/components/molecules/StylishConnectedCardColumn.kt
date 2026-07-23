package com.segnities007.stylishui.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.foundation.connectedColumnCorners
import com.segnities007.stylishui.foundation.connectedColumnEdges
import com.segnities007.stylishui.foundation.connectedShape
import com.segnities007.stylishui.theme.StylishTheme
import com.segnities007.stylishui.tokens.StylishDimensions

@Composable
fun StylishConnectedCardColumn(
    items: List<StylishConnectedCardItem>,
    modifier: Modifier = Modifier,
    spacing: Dp = StylishDimensions.connectedSpacing,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items.forEachIndexed { index, item ->
            StylishConnectedCard(
                title = item.title,
                supportingText = item.supportingText,
                onClick = item.onClick,
                onLongClick = item.onLongClick,
                shape = connectedShape(connectedColumnCorners(index, items.size)),
                outlineEdges = connectedColumnEdges(index, items.size),
                outlineCorners = connectedColumnCorners(index, items.size),
                trailingContent = item.trailingContent,
            )
        }
    }
}

@Preview(name = "Connected card column", showBackground = true, widthDp = 393)
@Composable
private fun StylishConnectedCardColumnPreview() {
    StylishTheme(darkTheme = false) {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedCardColumn(
                listOf(
                    StylishConnectedCardItem("今日", "3件の予定"),
                    StylishConnectedCardItem("明日", "1件の予定"),
                    StylishConnectedCardItem("今週", "8件の予定"),
                ),
            )
        }
    }
}
