package com.segnities007.stylishui.components.patterns

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.charts.BarChartData
import com.segnities007.stylishui.components.charts.SimpleBarChart
import com.segnities007.stylishui.theme.StylishTheme

@Composable
fun BarChartSection(
    title: String,
    data: List<BarChartData>,
    contentDescriptionPrefix: String,
    emptyLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StylishSectionTitle(title)
        SimpleBarChart(
            data = data,
            contentDescriptionPrefix = contentDescriptionPrefix,
            emptyLabel = emptyLabel,
        )
    }
}

@Preview(name = "Bar chart section", showBackground = true, widthDp = 393)
@Composable
private fun BarChartSectionPreview() {
    StylishTheme(darkTheme = false) {
        Surface(Modifier.padding(20.dp)) {
            BarChartSection(
                title = "月次費用",
                contentDescriptionPrefix = "棒グラフ",
                emptyLabel = "データがありません",
                data = listOf(
                    BarChartData("2月", 8000f),
                    BarChartData("3月", 12000f),
                    BarChartData("4月", 45000f),
                ),
            )
        }
    }
}
