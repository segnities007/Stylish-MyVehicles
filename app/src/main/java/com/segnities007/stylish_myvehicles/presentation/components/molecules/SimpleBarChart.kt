package com.segnities007.stylish_myvehicles.presentation.components.molecules

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class BarChartData(
    val label: String,
    val value: Float,
)

@Composable
fun SimpleBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    val maxValue = if (data.isNotEmpty()) data.maxOf { it.value }
        .coerceAtLeast(1f)
    else 1f
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val description = "棒グラフ: " + data.joinToString(", ") {
        "${it.label}=${
            String.format(
                "%,d",
                it.value.toInt()
            )
        }"
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .semantics { contentDescription = description },
    ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val bottomPadding = 28.dp.toPx()
        val topPadding = 8.dp.toPx()
        val leftPadding = 40.dp.toPx()
        val usableWidth = chartWidth - leftPadding
        val usableHeight = chartHeight - bottomPadding - topPadding

        val labelPaint = Paint().apply {
            color = labelColor
            textSize = 10.dp.toPx()
            isAntiAlias = true
            typeface = Typeface.DEFAULT
        }

        for (i in 0..3) {
            val y = topPadding + usableHeight * i / 3
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(chartWidth, y),
                strokeWidth = 1f,
            )
            val gridValue = maxValue * (3 - i) / 3
            drawContext.canvas.nativeCanvas.drawText(
                formatCompact(gridValue),
                2.dp.toPx(),
                y + 4.dp.toPx(),
                labelPaint,
            )
        }

        if (data.isEmpty()) {
            labelPaint.textAlign = Paint.Align.CENTER
            labelPaint.color = labelColor
            drawContext.canvas.nativeCanvas.drawText(
                "データがありません",
                chartWidth / 2f,
                chartHeight / 2f,
                labelPaint,
            )
        }
        else {
            data.forEachIndexed { index, d ->
                val barHeight = (d.value / maxValue) * usableHeight
                val barWidth = usableWidth / (data.size * 2f + 1)
                val spacing = barWidth
                val x = leftPadding + spacing + index * (barWidth + spacing)
                val y = chartHeight - bottomPadding - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                )

                labelPaint.textAlign = Paint.Align.CENTER
                drawContext.canvas.nativeCanvas.drawText(
                    d.label,
                    x + barWidth / 2,
                    chartHeight - 8.dp.toPx(),
                    labelPaint,
                )
            }
        }
    }
}

internal fun formatCompact(value: Float): String = when {
    value >= 10_000 -> "%.1f万".format(value / 10_000)
    value >= 1_000 -> "%.1fk".format(value / 1_000)
    else -> "%.0f".format(value)
}

@Preview(name = "Simple bar chart", showBackground = true, widthDp = 393)
@Composable
private fun SimpleBarChartPreview() {
    MaterialTheme {
        SimpleBarChart(
            data = listOf(
                BarChartData("1月", 30000f),
                BarChartData("2月", 45000f),
                BarChartData("3月", 28000f),
                BarChartData("4月", 52000f),
                BarChartData("5月", 41000f),
            ),
        )
    }
}

@Preview(name = "Simple bar chart (empty)", showBackground = true, widthDp = 393)
@Composable
private fun SimpleBarChartEmptyPreview() {
    MaterialTheme {
        SimpleBarChart(data = emptyList())
    }
}
