package com.segnities007.stylish_mycars.presentation.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

data class PieChartData(
    val label: String,
    val value: Float,
    val color: Color,
)

@Composable
fun SimplePieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    if (total <= 0f) return

    val description = data.joinToString(", ") {
        "${it.label}: ${String.format("%,d", it.value.toInt())}"
    }
    val holeColor = MaterialTheme.colorScheme.surface

    Canvas(
        modifier = modifier
            .size(160.dp)
            .semantics { contentDescription = "円グラフ: $description" },
    ) {
        var startAngle = -90f
        data.forEach { slice ->
            val sweep = (slice.value / total) * 360f
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
            )
            startAngle += sweep
        }
        drawCircle(
            color = holeColor,
            radius = size.minDimension * 0.3f,
        )
    }
}

@Composable
fun costCategoryColor(index: Int): Color {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.outline,
    )
    return colors[index % colors.size]
}
