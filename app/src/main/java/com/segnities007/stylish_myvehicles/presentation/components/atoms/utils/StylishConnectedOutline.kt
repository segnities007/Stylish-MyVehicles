package com.segnities007.stylish_myvehicles.presentation.components.atoms.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class StylishConnectedEdges(
    val top: Boolean,
    val end: Boolean,
    val bottom: Boolean,
    val start: Boolean,
) {
    companion object {
        val All = StylishConnectedEdges(true, true, true, true)
    }
}

fun stylishConnectedRowEdges(index: Int, size: Int) = StylishConnectedEdges(
    top = true,
    end = index == size - 1,
    bottom = true,
    start = index == 0,
)

fun stylishConnectedColumnEdges(index: Int, size: Int) = StylishConnectedEdges(
    top = index == 0,
    end = true,
    bottom = index == size - 1,
    start = true,
)

/**
 * Connected要素の露出している辺だけを描く。
 * 接続辺を描かないため、要素間のspacingに合わせて外周線も自然に途切れる。
 */
@Composable
fun Modifier.stylishConnectedOutline(
    edges: StylishConnectedEdges,
    corners: StylishConnectedCorners,
    cornerRadius: Dp = 12.dp,
): Modifier {
    val color = MaterialTheme.colorScheme.outlineVariant
    return drawWithContent {
        drawContent()

        val strokeWidth = 1.dp.toPx()
        val inset = strokeWidth / 2f
        val left = inset
        val top = inset
        val right = size.width - inset
        val bottom = size.height - inset
        val radius = cornerRadius.toPx().coerceAtMost(minOf(size.width, size.height) / 2f)
        val path = Path()

        if (edges.top) {
            path.moveTo(if (corners.topStart && edges.start) left + radius else left, top)
            path.lineTo(if (corners.topEnd && edges.end) right - radius else right, top)
        }
        if (corners.topEnd && edges.top && edges.end) {
            path.moveTo(right - radius, top)
            path.quadraticTo(right, top, right, top + radius)
        }
        if (edges.end) {
            path.moveTo(right, if (corners.topEnd && edges.top) top + radius else top)
            path.lineTo(right, if (corners.bottomEnd && edges.bottom) bottom - radius else bottom)
        }
        if (corners.bottomEnd && edges.bottom && edges.end) {
            path.moveTo(right, bottom - radius)
            path.quadraticTo(right, bottom, right - radius, bottom)
        }
        if (edges.bottom) {
            path.moveTo(if (corners.bottomStart && edges.start) left + radius else left, bottom)
            path.lineTo(if (corners.bottomEnd && edges.end) right - radius else right, bottom)
        }
        if (corners.bottomStart && edges.bottom && edges.start) {
            path.moveTo(left + radius, bottom)
            path.quadraticTo(left, bottom, left, bottom - radius)
        }
        if (edges.start) {
            path.moveTo(left, if (corners.topStart && edges.top) top + radius else top)
            path.lineTo(left, if (corners.bottomStart && edges.bottom) bottom - radius else bottom)
        }
        if (corners.topStart && edges.top && edges.start) {
            path.moveTo(left, top + radius)
            path.quadraticTo(left, top, left + radius, top)
        }

        drawPath(path, color = color, style = Stroke(strokeWidth))
    }
}
