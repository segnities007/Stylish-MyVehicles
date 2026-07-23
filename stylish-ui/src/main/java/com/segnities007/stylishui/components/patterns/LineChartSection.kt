package com.segnities007.stylishui.components.patterns

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.charts.LineChartData
import com.segnities007.stylishui.components.charts.SimpleLineChart
import com.segnities007.stylishui.theme.StylishTheme

@Composable
fun LineChartSection(
    title: String,
    data: List<LineChartData>,
    contentDescriptionPrefix: String,
    emptyLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StylishSectionTitle(title)
        SimpleLineChart(
            data = data,
            contentDescriptionPrefix = contentDescriptionPrefix,
            emptyLabel = emptyLabel,
        )
    }
}

@Preview(name = "Line chart section", showBackground = true, widthDp = 393)
@Composable
private fun LineChartSectionPreview() {
    StylishTheme(darkTheme = false) {
        Surface(Modifier.padding(20.dp)) {
            LineChartSection(
                title = "燃費推移 (km/L)",
                contentDescriptionPrefix = "折れ線グラフ",
                emptyLabel = "データがありません",
                data = listOf(
                    LineChartData("5/2", 17.2f),
                    LineChartData("5/16", 18.1f),
                    LineChartData("5/30", 17.8f),
                ),
            )
        }
    }
}
