package com.segnities007.stylishui.components.molecules

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.models.StylishConnectedButtonItem
import com.segnities007.stylishui.components.models.StylishSegmentedOption
import com.segnities007.stylishui.theme.StylishTheme

@Composable
fun <T> StylishConnectedSegmentedControl(
    options: List<StylishSegmentedOption<T>>,
    selectedValue: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
    StylishConnectedButtonRow(
        items = options.map { option ->
            StylishConnectedButtonItem(
                onClick = { onSelected(option.value) },
                colors = selectedColors.takeIf { option.value == selectedValue },
                leadingContent = option.leadingContent,
            ) { Text(option.label) }
        },
        modifier = modifier,
    )
}

@Preview(name = "Connected segmented control", showBackground = true, widthDp = 393)
@Composable
private fun StylishConnectedSegmentedControlPreview() {
    StylishTheme(darkTheme = false) {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedSegmentedControl(
                options = listOf(
                    StylishSegmentedOption("list", "リスト") {
                        Icon(Icons.AutoMirrored.Filled.ViewList, null)
                    },
                    StylishSegmentedOption("grid", "グリッド") {
                        Icon(Icons.Default.GridView, null)
                    },
                ),
                selectedValue = "list",
                onSelected = {},
            )
        }
    }
}
