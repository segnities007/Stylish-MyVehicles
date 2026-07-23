package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.charts.PieChartData
import com.segnities007.stylishui.components.charts.SimplePieChart
import com.segnities007.stylishui.components.patterns.StylishSectionTitle
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun PieChartSection(
    title: String,
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    legendSpacing: Dp = 6.dp,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StylishSectionTitle(title)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SimplePieChart(
                data = data,
                contentDescriptionPrefix = "円グラフ",
            )
            if (data.isNotEmpty()) {
                Legend(data = data, spacing = legendSpacing)
            }
        }
    }
}

@Composable
private fun Legend(
    data: List<PieChartData>,
    spacing: Dp,
) {
    Column {
        data.forEach { slice ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .padding(end = spacing)
                        .size(10.dp)
                        .background(slice.color, CircleShape),
                )
                Text(
                    "${slice.label}: ${String.format("%,d", slice.value.toInt())}円",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview(name = "Pie chart section", showBackground = true, widthDp = 393)
@Composable
private fun PieChartSectionPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            PieChartSection(
                title = "費用カテゴリ",
                data = listOf(
                    PieChartData("給油", 120000f, MaterialTheme.colorScheme.primary),
                    PieChartData("整備", 80000f, MaterialTheme.colorScheme.tertiary),
                    PieChartData("保険", 60000f, MaterialTheme.colorScheme.secondary),
                ),
            )
        }
    }
}
