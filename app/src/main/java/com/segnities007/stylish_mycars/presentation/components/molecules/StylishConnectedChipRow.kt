package com.segnities007.stylish_mycars.presentation.components.molecules

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedRowCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

@Composable
fun StylishConnectedChipRow(
    items: List<StylishConnectedChipItem>,
    modifier: Modifier = Modifier,
    spacing: Dp = 2.dp,
    fillWidth: Boolean = false,
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = if (fillWidth) modifier.fillMaxWidth()
        else modifier.horizontalScroll(
            rememberScrollState()
        ),
        horizontalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items.forEachIndexed { index, item ->
            val containerColor by animateColorAsState(
                targetValue = if (item.selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainerHighest,
                animationSpec = tween(180),
                label = "chipContainer",
            )
            val contentColor by animateColorAsState(
                targetValue = if (item.selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(180),
                label = "chipContent",
            )
            Surface(
                modifier = Modifier
                    .let { if (fillWidth) it.weight(1f) else it }
                    .semantics {
                        selected = item.selected
                        role = Role.Tab
                    }
                    .clickable(enabled = item.enabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        item.onClick()
                    },
                shape = stylishConnectedShape(stylishConnectedRowCorners(index, items.size)),
                color = containerColor,
                contentColor = contentColor,
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item.leadingContent?.invoke(this)
                    Text(item.label, style = MaterialTheme.typography.labelLarge)
                    item.trailingContent?.invoke(this)
                }
            }
        }
    }
}

@Preview(name = "Connected chip row", showBackground = true, widthDp = 393)
@Composable
private fun StylishConnectedChipRowPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedChipRow(
                listOf(
                    StylishConnectedChipItem("すべて", {}, selected = true) {
                        Icon(Icons.Default.Check, null)
                    },
                    StylishConnectedChipItem("仕事", {}),
                    StylishConnectedChipItem("個人", {}),
                    StylishConnectedChipItem("アイデア", {}),
                ),
            )
        }
    }
}
