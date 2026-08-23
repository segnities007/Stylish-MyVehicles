package com.segnities007.stylish_myvehicles.presentation.screen.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.domain.service.DeadlineInfo
import com.segnities007.stylishui.components.atoms.StylishConnectedCard
import com.segnities007.stylishui.components.atoms.StylishSectionTitle
import com.segnities007.stylishui.components.molecules.StylishConnectedCardColumn
import com.segnities007.stylishui.components.molecules.StylishEmptyState
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishBottomBar
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onAddRecord: () -> Unit,
    onNavigateToHome: () -> Unit,
    bottomBarVisible: MutableState<Boolean>? = null,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isAtTop by remember(listState) {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset <= 0
        }
    }
    LaunchedEffect(isAtTop) {
        bottomBarVisible?.value = isAtTop
    }

    StylishScaffold(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                state = listState,
            ) {
                item {
                    StylishHeader(
                        title = { Text(stringResource(R.string.notifications_title)) },
                    )
                    Spacer(Modifier.height(8.dp))
                }

                when {
                    state.isLoading -> {
                        item {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 80.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    state.deadlines.isEmpty() -> {
                        item {
                            StylishEmptyState(
                                icon = Icons.Default.Notifications,
                                title = stringResource(R.string.no_notifications_title),
                                description = stringResource(R.string.no_notifications_description),
                            )
                        }
                    }

                    else -> {
                        val mostImportant = state.deadlines.first()
                        val rest = state.deadlines.drop(1)
                        val expired = rest.filter { it.deadline.isExpired }
                        val urgent = rest.filter {
                            !it.deadline.isExpired && it.deadline.isUrgent
                        }
                        val upcoming = rest.filter {
                            !it.deadline.isExpired && !it.deadline.isUrgent
                        }

                        item {
                            MostImportantDeadlineCard(item = mostImportant)
                            Spacer(Modifier.height(16.dp))
                        }

                        if (expired.isNotEmpty()) {
                            item {
                                StylishSectionTitle(stringResource(R.string.expired_section))
                            }
                            item {
                                DeadlineList(items = expired)
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        if (urgent.isNotEmpty()) {
                            item {
                                StylishSectionTitle(stringResource(R.string.expiring_soon_section))
                            }
                            item {
                                DeadlineList(items = urgent)
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        if (upcoming.isNotEmpty()) {
                            item {
                                StylishSectionTitle(stringResource(R.string.upcoming_section))
                            }
                            item {
                                DeadlineList(items = upcoming)
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MostImportantDeadlineCard(item: VehicleDeadlineItem) {
    val daysText = if (item.deadline.isExpired) {
        stringResource(R.string.expired_days_over, -item.deadline.daysRemaining)
    } else {
        stringResource(R.string.days_remaining, item.deadline.daysRemaining)
    }
    StylishConnectedCard(
        title = "${item.vehicleName} / ${item.deadline.label}",
        supportingText = "${item.deadline.date} （$daysText）",
        trailingContent = {
            Text(
                stringResource(R.string.most_important),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
        },
    )
}

@Composable
private fun DeadlineList(items: List<VehicleDeadlineItem>) {
    StylishConnectedCardColumn(
        spacing = 4.dp,
        items = items.map { item ->
            val daysText = if (item.deadline.isExpired) {
                stringResource(R.string.expired_days_over, -item.deadline.daysRemaining)
            } else {
                stringResource(R.string.days_remaining, item.deadline.daysRemaining)
            }
            StylishConnectedCardItem(
                title = "${item.vehicleName} / ${item.deadline.label}",
                supportingText = "${item.deadline.date} （$daysText）",
                trailingContent = {
                    Text(
                        daysText,
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            item.deadline.isExpired -> MaterialTheme.colorScheme.error
                            item.deadline.isUrgent -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
            )
        },
    )
}

@Preview(name = "NotificationScreen", showBackground = true, widthDp = 393)
@Composable
private fun NotificationScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            Column {
                StylishSectionTitle("まもなく期限")
                DeadlineList(
                    items = listOf(
                        VehicleDeadlineItem(
                            vehicleId = 1L,
                            vehicleName = "カローラ",
                            deadline = DeadlineInfo(
                                "車検",
                                LocalDate.now().plusDays(20),
                                20,
                            ),
                        ),
                        VehicleDeadlineItem(
                            vehicleId = 1L,
                            vehicleName = "カローラ",
                            deadline = DeadlineInfo(
                                "オイル交換",
                                LocalDate.now().plusDays(10),
                                10,
                            ),
                        ),
                    ),
                )
                Spacer(Modifier.height(16.dp))
                StylishSectionTitle("今後の予定")
                DeadlineList(
                    items = listOf(
                        VehicleDeadlineItem(
                            vehicleId = 1L,
                            vehicleName = "カローラ",
                            deadline = DeadlineInfo(
                                "任意保険",
                                LocalDate.now().plusDays(120),
                                120,
                            ),
                        ),
                    ),
                )
            }
        }
    }
}
