package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishRoundedIconButton
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun StylishBottomBar(
    onNavigateToHome: () -> Unit,
    onAddRecord: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToRecordsList: (() -> Unit)? = null,
    scrollState: LazyListState? = null,
) {
    val targetAlpha = if (scrollState == null || (scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset <= 0)) 1f else 0f
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(300),
        label = "bottomBarAlpha",
    )
    // フェードアウト中はボタンを無効化し、見えない状態でのタップ判定（ヒットエリア）を残さない。
    val interactive = targetAlpha == 1f

    Surface(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .alpha(alpha),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StylishRoundedIconButton(
                imageVector = Icons.Default.Home,
                contentDescription = "ホーム",
                onClick = onNavigateToHome,
                enabled = interactive,
            )
            if (onNavigateToRecordsList != null) {
                StylishRoundedIconButton(
                    imageVector = Icons.Default.History,
                    contentDescription = "記録一覧",
                    onClick = onNavigateToRecordsList,
                    enabled = interactive,
                )
            }
            StylishRoundedIconButton(
                imageVector = Icons.Default.Add,
                contentDescription = "記録を追加",
                onClick = onAddRecord,
                enabled = interactive,
            )
            StylishRoundedIconButton(
                imageVector = Icons.Default.Notifications,
                contentDescription = "通知",
                onClick = onNavigateToNotifications,
                enabled = interactive,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393)
@Composable
private fun StylishBottomBarPreview() {
    StylishMyVehiclesTheme {
        StylishBottomBar(
            onNavigateToHome = {},
            onAddRecord = {},
            onNavigateToNotifications = {},
        )
    }
}
