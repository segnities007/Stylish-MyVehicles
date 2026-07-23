package com.segnities007.stylish_myvehicles.presentation.screen.records

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishPageContent
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.components.PagerIndicator

/**
 * 記録画面（給油/整備/費用）共通のページャー殻。
 * 期間ページャー・ヘッダー・粒度メニュー・FABスロットを提供し、
 * ページごとの中身（グラフ・サマリー・一覧）は [periodContent] で各トピック画面が描画する。
 */
@Composable
fun RecordsLayout(
    viewModel: RecordsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    periodContent: LazyListScope.(Period) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RecordsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = state.periods.size,
        pageCount = { state.periods.size + 1 },
    )
    LaunchedEffect(pagerState.currentPage) {
        viewModel.accept(RecordsIntent.PageChanged(pagerState.currentPage))
    }

    // ホーム画面と同じスクロールUI。現在のページが最上部にいる間だけFABを表示する
    val pageListStates = remember(state.periods.size) {
        List(state.periods.size + 1) { LazyListState() }
    }
    val currentListState = pageListStates[pagerState.currentPage]
    val isAtTop by remember(currentListState) {
        derivedStateOf {
            currentListState.firstVisibleItemIndex == 0 &&
                    currentListState.firstVisibleItemScrollOffset <= 0
        }
    }

    val onChangePeriodMode: (PeriodMode) -> Unit = { mode ->
        PeriodPreference.setMode(context, mode)
        viewModel.accept(RecordsIntent.ChangePeriodMode(mode))
    }

    StylishScaffold(
        modifier = modifier,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isAtTop,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
            ) {
                floatingActionButton()
            }
        },
    ) {
        Box(Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.periods.isEmpty() -> StylishEmptyState(
                    icon = Icons.Default.AttachMoney,
                    title = "記録がありません",
                    description = "給油・整備・費用を記録しましょう",
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        if (page == 0) {
                            NoDataBoundaryPage(
                                listState = pageListStates[page],
                                onNavigateBack = { viewModel.accept(RecordsIntent.NavigateBack) },
                            )
                        } else {
                            val period = state.periods.getOrNull(page - 1) ?: return@HorizontalPager
                            PeriodShell(
                                period = period,
                                listState = pageListStates[page],
                                onNavigateBack = { viewModel.accept(RecordsIntent.NavigateBack) },
                                onChangePeriodMode = onChangePeriodMode,
                            ) {
                                periodContent(period)
                            }
                        }
                    }
                    if (pagerState.pageCount > 1) {
                        PagerIndicator(
                            pagerState = pagerState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * 期間ページのヘッダー（期間ラベル・戻る・粒度メニュー）とスクロール骨格。
 */
@Composable
internal fun PeriodShell(
    period: Period,
    listState: LazyListState,
    onNavigateBack: () -> Unit,
    onChangePeriodMode: (PeriodMode) -> Unit,
    content: LazyListScope.() -> Unit,
) {
    var showModeMenu by remember { mutableStateOf(false) }

    StylishPageContent(
        listState = listState,
        header = {
            StylishHeader(
                title = {
                    Text(
                        period.label,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                        onClick = onNavigateBack,
                    )
                },
                actions = {
                    Box {
                        StylishIconButton(
                            Icons.Default.Settings, "表示設定",
                            onClick = { showModeMenu = true },
                        )
                        DropdownMenu(
                            expanded = showModeMenu,
                            onDismissRequest = { showModeMenu = false },
                        ) {
                            PeriodMode.entries.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.label) },
                                    onClick = {
                                        showModeMenu = false
                                        onChangePeriodMode(m)
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
    ) {
        content()
    }
}

@Composable
private fun NoDataBoundaryPage(
    listState: LazyListState,
    onNavigateBack: () -> Unit,
) {
    StylishPageContent(
        listState = listState,
        header = {
            StylishHeader(
                title = {},
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                        onClick = onNavigateBack,
                    )
                },
            )
        },
    ) {
        item {
            StylishEmptyState(
                icon = Icons.Default.AttachMoney,
                title = "これより過去のデータはありません",
                description = "古い記録はありません",
            )
        }
    }
}
