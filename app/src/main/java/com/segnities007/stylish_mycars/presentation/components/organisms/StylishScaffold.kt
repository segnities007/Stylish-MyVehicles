package com.segnities007.stylish_mycars.presentation.components.organisms

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme

@Composable
fun StylishScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
    ) {
        // innerPadding を content に渡さない
        // - ステータスバー: StylishHeader.statusBarsPadding() で各画面が管理
        // - ナビゲーションバー: 渡すと fillMaxSize + verticalScroll との組み合わせで
        //   コンテンツが押し上げられる原因になる (contentWindowInsets は FAB 配置用)
        // 各画面は必要なら独自に .navigationBarsPadding() を当てる
        content()
    }
}

@Preview(name = "StylishScaffold", showBackground = true, widthDp = 393)
@Composable
private fun StylishScaffoldPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            StylishScaffold {
                Text("StylishScaffold with content")
            }
        }
    }
}
