package com.segnities007.stylish_myvehicles.presentation.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.utils.stylishConnectedRowCorners
import com.segnities007.stylish_myvehicles.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun StylishConnectedCardRow(
    items: List<StylishConnectedCardItem>,
    modifier: Modifier = Modifier,
    spacing: Dp = 4.dp,
) {
    Row(
        modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items.forEachIndexed { index, item ->
            StylishConnectedCard(
                title = item.title,
                supportingText = item.supportingText,
                onClick = item.onClick,
                onLongClick = item.onLongClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = stylishConnectedShape(stylishConnectedRowCorners(index, items.size)),
                trailingContent = item.trailingContent,
            )
        }
    }
}

@Preview(name = "Connected card row", showBackground = true, widthDp = 393)
@Composable
private fun StylishConnectedCardRowPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedCardRow(
                listOf(
                    StylishConnectedCardItem("12", "メモ"),
                    StylishConnectedCardItem("3", "お気に入り"),
                ),
            )
        }
    }
}
