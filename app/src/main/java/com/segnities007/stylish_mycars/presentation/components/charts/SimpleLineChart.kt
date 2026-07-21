package com.segnities007.stylish_mycars.presentation.components.charts

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

data class LineChartData(
    val label: String,
    val value: Float,
)

@Composable
fun SimpleLineChart(
    data: List<LineChartData>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    pointColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    if (data.size < 2) return

    val values = data.map { it.value }
    val minValue = values.min()
    val maxValue = values.max()
    val range = (maxValue - minValue).coerceAtLeast(1f)
    val padding = range * 0.1f

    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val pointInnerColor = MaterialTheme.colorScheme.surface
    val description = "折れ線グラフ: " + data.joinToString(", ") { "${it.label}=${it.value}" }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .semantics { contentDescription = description },
    ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val bottomPadding = 32.dp.toPx()
        val topPadding = 12.dp.toPx()
        val leftPadding = 40.dp.toPx()
        val usableWidth = chartWidth - leftPadding
        val usableHeight = chartHeight - bottomPadding - topPadding

        val labelPaint = Paint().apply {
            color = labelColor
            textSize = 10.dp.toPx()
            isAntiAlias = true
            typeface = Typeface.DEFAULT
        }

        // Y軸目盛り（5段階）
        for (i in 0..4) {
            val y = topPadding + usableHeight * i / 4
            drawLine(
                color = gridColor,
                start = Offset(leftPadding, y),
                end = Offset(chartWidth, y),
                strokeWidth = 1f,
            )
            val gridValue = maxValue + padding - (range + padding * 2) * i / 4
            drawContext.canvas.nativeCanvas.drawText(
                "%.1f".format(gridValue),
                2.dp.toPx(),
                y + 4.dp.toPx(),
                labelPaint,
            )
        }

        // データポイントの座標計算
        val points = data.mapIndexed { index, d ->
            val x = if (data.size > 1) {
                leftPadding + usableWidth * index / (data.size - 1)
            } else {
                leftPadding + usableWidth / 2
            }
            val normalized = (d.value - minValue + padding) / (range + padding * 2)
            val y = topPadding + usableHeight * (1 - normalized)
            Offset(x, y)
        }

        // 塗りつぶし領域
        val fillPath = Path().apply {
            moveTo(points.first().x, chartHeight - bottomPadding)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, chartHeight - bottomPadding)
            close()
        }
        drawPath(fillPath, fillColor)

        // 折れ線
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }
        drawPath(
            linePath,
            lineColor,
            style = Stroke(width = 2.5.dp.toPx()),
        )

        // データポイント
        points.forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 4.dp.toPx(),
                center = point,
            )
            drawCircle(
                color = pointInnerColor,
                radius = 2.dp.toPx(),
                center = point,
            )
        }

        // X軸ラベル（間引き表示）
        val maxLabels = 6
        val step = (data.size + maxLabels - 1) / maxLabels
        data.forEachIndexed { index, d ->
            if (index % step == 0 || index == data.size - 1) {
                val x = points[index].x
                labelPaint.textAlign = Paint.Align.CENTER
                drawContext.canvas.nativeCanvas.drawText(
                    d.label,
                    x.coerceIn(leftPadding, chartWidth - 10.dp.toPx()),
                    chartHeight - 8.dp.toPx(),
                    labelPaint,
                )
            }
        }
    }
}
