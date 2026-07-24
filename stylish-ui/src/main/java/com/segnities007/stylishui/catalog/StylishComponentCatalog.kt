package com.segnities007.stylishui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.models.StylishConnectedButtonItem
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.components.models.StylishConnectedChipItem
import com.segnities007.stylishui.components.models.StylishConnectedListItem
import com.segnities007.stylishui.components.molecules.StylishConnectedButtonRow
import com.segnities007.stylishui.components.molecules.StylishConnectedCardGrid
import com.segnities007.stylishui.components.molecules.StylishConnectedChipRow
import com.segnities007.stylishui.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylishui.theme.StylishTheme

@Composable
private fun ComponentCatalog() {
    Surface {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            StylishConnectedCardGrid(
                items = listOf(
                    StylishConnectedCardItem(
                        title = "Actionable",
                        supportingText = "Click and elevation",
                        onClick = {},
                    ),
                    StylishConnectedCardItem(
                        title = "Read only",
                        supportingText = "No click or elevation",
                    ),
                ),
                columns = 2,
            )
            StylishConnectedButtonRow(
                items = listOf(
                    StylishConnectedButtonItem(onClick = {}) { Text("Enabled") },
                    StylishConnectedButtonItem { Text("No action") },
                ),
            )
            StylishConnectedChipRow(
                items = listOf(
                    StylishConnectedChipItem("Selected", {}, selected = true),
                    StylishConnectedChipItem("Read only"),
                ),
            )
            StylishConnectedListItemColumn(
                items = listOf(
                    StylishConnectedListItem("Actionable item", onClick = {}),
                    StylishConnectedListItem("Read-only item"),
                    StylishConnectedListItem("Disabled item", enabled = false),
                ),
            )
        }
    }
}

@Preview(name = "StylishUI catalog · Light", showBackground = true, widthDp = 393)
@Composable
private fun ComponentCatalogLightPreview() {
    StylishTheme(darkTheme = false) {
        ComponentCatalog()
    }
}

@Preview(name = "StylishUI catalog · Dark", showBackground = true, widthDp = 393)
@Composable
private fun ComponentCatalogDarkPreview() {
    StylishTheme(darkTheme = true) {
        ComponentCatalog()
    }
}
