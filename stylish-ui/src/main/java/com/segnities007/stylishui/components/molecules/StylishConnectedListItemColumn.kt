package com.segnities007.stylishui.components.molecules

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.components.models.StylishConnectedListItem
import com.segnities007.stylishui.foundation.connectedColumnCorners
import com.segnities007.stylishui.foundation.connectedColumnEdges
import com.segnities007.stylishui.foundation.connectedOutline
import com.segnities007.stylishui.foundation.connectedShape
import com.segnities007.stylishui.foundation.isActionable
import com.segnities007.stylishui.theme.StylishTheme
import com.segnities007.stylishui.theme.stylishComponentColors
import com.segnities007.stylishui.tokens.StylishDimensions

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StylishConnectedListItemColumn(
    items: List<StylishConnectedListItem>,
    modifier: Modifier = Modifier,
    spacing: Dp = StylishDimensions.connectedSpacing,
) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items.forEachIndexed { index, item ->
            val corners = connectedColumnCorners(index, items.size)
            val actionable = isActionable(
                enabled = item.enabled,
                hasClickAction = item.onClick != null,
                hasLongClickAction = item.onLongClick != null,
            )
            Surface(
                modifier = Modifier
                    .then(
                        if (actionable) {
                            Modifier
                                .combinedClickable(
                                    onClick = { item.onClick?.invoke() },
                                    onLongClick = item.onLongClick?.let {
                                        {
                                            haptic.performHapticFeedback(
                                                HapticFeedbackType.LongPress,
                                            )
                                            it()
                                        }
                                    },
                                )
                                .semantics { role = Role.Button }
                        } else {
                            Modifier
                        },
                    )
                    .semantics {
                        if (!item.enabled) disabled()
                    }
                    .connectedOutline(
                        edges = connectedColumnEdges(index, items.size),
                        corners = corners,
                    ),
                shape = connectedShape(corners),
                color = MaterialTheme.stylishComponentColors.groupedContainer,
                contentColor = if (item.enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                shadowElevation = if (actionable) StylishDimensions.interactiveElevation else 0.dp,
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item.leadingContent?.invoke(this)
                    Column(Modifier.weight(1f)) {
                        Text(item.headline, style = MaterialTheme.typography.titleMedium)
                        item.supportingText?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        item.supportingLines.forEach { line ->
                            Text(
                                line,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    item.trailingContent?.invoke(this)
                }
            }
        }
    }
}

@Preview(name = "Connected list items", showBackground = true, widthDp = 393)
@Composable
private fun StylishConnectedListItemColumnPreview() {
    StylishTheme(darkTheme = false) {
        Surface(Modifier.padding(20.dp)) {
            StylishConnectedListItemColumn(
                listOf(
                    StylishConnectedListItem(
                        headline = "テーマ",
                        supportingText = "システム設定を使用",
                        onClick = {},
                        leadingContent = { Icon(Icons.Default.Palette, null) },
                        trailingContent = {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                        },
                    ),
                    StylishConnectedListItem(
                        headline = "通知",
                        onClick = {},
                        leadingContent = { Icon(Icons.Default.Notifications, null) },
                        trailingContent = { Switch(true, {}) },
                    ),
                ),
            )
        }
    }
}
