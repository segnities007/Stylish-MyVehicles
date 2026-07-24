package com.segnities007.stylishui.components.charts

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

/** 積み上げ棒グラフの1区間。value と色。 */
data class BarChartSegment(
    val value: Float,
    val color: Color,
)

data class BarChartData(
    val label: String,
    val value: Float,
    /** カテゴリ別の内訳。空の場合は単色の棒として描画する。 */
    val segments: List<BarChartSegment> = emptyList(),
)

@Composable
fun SimpleBarChart(
    data: List<BarChartData>,
    contentDescriptionPrefix: String,
    emptyLabel: String,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    val maxValue = if (data.isNotEmpty()) data.maxOf { it.value }
        .coerceAtLeast(1f)
    else 1f
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val description = "$contentDescriptionPrefix: " + data.joinToString(", ") {
        "${it.label}=${
            String.format(
                Locale.getDefault(),
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
                emptyLabel,
                chartWidth / 2f,
                chartHeight / 2f,
                labelPaint,
            )
        }
        else {
            val topRadius = 4.dp.toPx()
            data.forEachIndexed { index, d ->
                val barHeight = (d.value / maxValue) * usableHeight
                val barWidth = usableWidth / (data.size * 2f + 1)
                val spacing = barWidth
                val x = leftPadding + spacing + index * (barWidth + spacing)
                val y = chartHeight - bottomPadding - barHeight

                if (d.segments.isEmpty()) {
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(topRadius, topRadius),
                    )
                }
                else {
                    // セグメントを下から順に積み上げる。最上段のみ上部を角丸にする。
                    val lastNonZero = d.segments.indexOfLast { it.value > 0f }
                    var accumulated = 0f
                    d.segments.forEachIndexed { segIdx, seg ->
                        val segHeight = (seg.value / maxValue) * usableHeight
                        if (segHeight > 0f) {
                            val segTop = chartHeight - bottomPadding - accumulated - segHeight
                            if (segIdx == lastNonZero) {
                                drawTopRoundedRect(
                                    color = seg.color,
                                    left = x,
                                    top = segTop,
                                    width = barWidth,
                                    height = segHeight,
                                    radius = topRadius,
                                )
                            }
                            else {
                                drawRect(
                                    color = seg.color,
                                    topLeft = Offset(x, segTop),
                                    size = Size(barWidth, segHeight),
                                )
                            }
                        }
                        accumulated += segHeight
                    }
                }

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
    value >= 10_000 -> String.format(Locale.getDefault(), "%.1f万", value / 10_000)
    value >= 1_000 -> String.format(Locale.getDefault(), "%.1fk", value / 1_000)
    else -> String.format(Locale.getDefault(), "%.0f", value)
}

/** 上部2隅のみ角丸の矩形を描画する（積み上げ棒の最上段用）。 */
private fun DrawScope.drawTopRoundedRect(
    color: Color,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    radius: Float,
) {
    if (height <= 0f || width <= 0f) return
    val r = radius.coerceAtMost(height).coerceAtMost(width / 2f)
    val path = Path().apply {
        moveTo(left, top + height)
        lineTo(left, top + r)
        quadraticTo(left, top, left + r, top)
        lineTo(left + width - r, top)
        quadraticTo(left + width, top, left + width, top + r)
        lineTo(left + width, top + height)
        close()
    }
    drawPath(path, color)
}

@Preview(name = "Simple bar chart", showBackground = true, widthDp = 393)
@Composable
private fun SimpleBarChartPreview() {
    MaterialTheme {
        SimpleBarChart(
            contentDescriptionPrefix = "棒グラフ",
            emptyLabel = "データがありません",
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
        SimpleBarChart(
            data = emptyList(),
            contentDescriptionPrefix = "棒グラフ",
            emptyLabel = "データがありません",
        )
    }
}

@Preview(name = "Stacked bar chart", showBackground = true, widthDp = 393)
@Composable
private fun StackedBarChartPreview() {
    MaterialTheme {
        SimpleBarChart(
            contentDescriptionPrefix = "棒グラフ",
            emptyLabel = "データがありません",
            data = listOf(
                BarChartData(
                    "1月", 45000f,
                    segments = listOf(
                        BarChartSegment(25000f, stylishChartColor(0)),
                        BarChartSegment(12000f, stylishChartColor(1)),
                        BarChartSegment(8000f, stylishChartColor(2)),
                    ),
                ),
                BarChartData(
                    "2月", 30000f,
                    segments = listOf(
                        BarChartSegment(18000f, stylishChartColor(0)),
                        BarChartSegment(12000f, stylishChartColor(1)),
                    ),
                ),
                BarChartData(
                    "3月", 52000f,
                    segments = listOf(
                        BarChartSegment(28000f, stylishChartColor(0)),
                        BarChartSegment(14000f, stylishChartColor(1)),
                        BarChartSegment(10000f, stylishChartColor(2)),
                    ),
                ),
            ),
        )
    }
}
