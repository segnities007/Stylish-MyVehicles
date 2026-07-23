package com.segnities007.stylishui.foundation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.segnities007.stylishui.tokens.StylishDimensions

@Immutable
data class ConnectedCorners(
    val topStart: Boolean = false,
    val topEnd: Boolean = false,
    val bottomStart: Boolean = false,
    val bottomEnd: Boolean = false,
) {
    companion object {
        val Standalone = ConnectedCorners(true, true, true, true)
    }
}

@Immutable
data class ConnectedEdges(
    val top: Boolean,
    val end: Boolean,
    val bottom: Boolean,
    val start: Boolean,
) {
    companion object {
        val All = ConnectedEdges(true, true, true, true)
    }
}

fun connectedShape(
    corners: ConnectedCorners,
    cornerRadius: Dp = StylishDimensions.connectedCornerRadius,
    joinedCornerRadius: Dp = StylishDimensions.joinedCornerRadius,
): Shape = RoundedCornerShape(
    topStart = if (corners.topStart) cornerRadius else joinedCornerRadius,
    topEnd = if (corners.topEnd) cornerRadius else joinedCornerRadius,
    bottomStart = if (corners.bottomStart) cornerRadius else joinedCornerRadius,
    bottomEnd = if (corners.bottomEnd) cornerRadius else joinedCornerRadius,
)

fun connectedColumnCorners(index: Int, size: Int) = ConnectedCorners(
    topStart = index == 0,
    topEnd = index == 0,
    bottomStart = index == size - 1,
    bottomEnd = index == size - 1,
)

fun connectedRowCorners(index: Int, size: Int) = ConnectedCorners(
    topStart = index == 0,
    bottomStart = index == 0,
    topEnd = index == size - 1,
    bottomEnd = index == size - 1,
)

fun connectedGridCorners(index: Int, size: Int, columns: Int): ConnectedCorners {
    require(columns > 0) { "columns must be greater than zero" }
    require(index in 0 until size) { "index must reference an existing item" }

    val column = index % columns
    val row = index / columns

    fun hasCell(targetRow: Int, targetColumn: Int): Boolean {
        if (targetColumn !in 0 until columns) return false
        return targetRow * columns + targetColumn in 0 until size
    }

    val hasAbove = hasCell(row - 1, column)
    val hasBelow = hasCell(row + 1, column)
    val hasLeft = hasCell(row, column - 1)
    val hasRight = hasCell(row, column + 1)

    return ConnectedCorners(
        topStart = !hasAbove && !hasLeft,
        topEnd = !hasAbove && !hasRight,
        bottomStart = !hasBelow && !hasLeft,
        bottomEnd = !hasBelow && !hasRight,
    )
}

fun connectedRowEdges(index: Int, size: Int): ConnectedEdges {
    require(index in 0 until size)
    return ConnectedEdges.All
}

fun connectedColumnEdges(index: Int, size: Int): ConnectedEdges {
    require(index in 0 until size)
    return ConnectedEdges.All
}
