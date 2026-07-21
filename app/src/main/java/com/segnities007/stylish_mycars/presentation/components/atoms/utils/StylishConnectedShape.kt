package com.segnities007.stylish_mycars.presentation.components.atoms.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class StylishConnectedCorners(
    val topStart: Boolean = false,
    val topEnd: Boolean = false,
    val bottomStart: Boolean = false,
    val bottomEnd: Boolean = false,
) {
    companion object {
        val Standalone = StylishConnectedCorners(true, true, true, true)
    }
}

fun stylishConnectedShape(
    corners: StylishConnectedCorners,
    cornerRadius: Dp = 12.dp,
    connectedCornerRadius: Dp = 2.dp,
): Shape = RoundedCornerShape(
    topStart = if (corners.topStart) cornerRadius else connectedCornerRadius,
    topEnd = if (corners.topEnd) cornerRadius else connectedCornerRadius,
    bottomStart = if (corners.bottomStart) cornerRadius else connectedCornerRadius,
    bottomEnd = if (corners.bottomEnd) cornerRadius else connectedCornerRadius,
)

fun stylishConnectedColumnCorners(index: Int, size: Int) = StylishConnectedCorners(
    topStart = index == 0,
    topEnd = index == 0,
    bottomStart = index == size - 1,
    bottomEnd = index == size - 1,
)

fun stylishConnectedRowCorners(index: Int, size: Int) = StylishConnectedCorners(
    topStart = index == 0,
    bottomStart = index == 0,
    topEnd = index == size - 1,
    bottomEnd = index == size - 1,
)

fun stylishConnectedGridCorners(index: Int, size: Int, columns: Int): StylishConnectedCorners {
    require(columns > 0) { "columns must be greater than zero" }
    val column = index % columns
    val row = index / columns

    // 隣接セルが埋まっているかで判定する。最終行が埋まっていない場合でも、
    // 空白セルに面した角が正しく丸くなる（例: 3要素・2列で右上セルの右下隅）。
    fun hasCell(r: Int, c: Int): Boolean {
        if (c < 0 || c >= columns) return false
        val i = r * columns + c
        return i in 0 until size
    }

    val hasAbove = hasCell(row - 1, column)
    val hasBelow = hasCell(row + 1, column)
    val hasLeft = hasCell(row, column - 1)
    val hasRight = hasCell(row, column + 1)

    return StylishConnectedCorners(
        topStart = !hasAbove && !hasLeft,
        topEnd = !hasAbove && !hasRight,
        bottomStart = !hasBelow && !hasLeft,
        bottomEnd = !hasBelow && !hasRight,
    )
}
