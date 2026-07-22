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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.domain.service.DeadlineInfo
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishConnectedListItemColumn
import com.segnities007.stylish_myvehicles.presentation.components.molecules.StylishEmptyState
import com.segnities007.stylish_myvehicles.presentation.components.molecules.models.StylishConnectedListItem
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishBottomBar
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishSectionTitle
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme
import java.time.LocalDate

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNavigateBack: () -> Unit,
    onAddRecord: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    StylishScaffold(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                state = listState,
            ) {
                item {
                    StylishHeader(
                        title = { Text("通知") },
                        navigation = {
                            StylishIconButton(
                                Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                                onClick = onNavigateBack,
                            )
                        },
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
                                title = "通知はありません",
                                description = "期限が設定されるとここに表示されます",
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
                                StylishSectionTitle("期限切れ")
                            }
                            item {
                                DeadlineList(items = expired)
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        if (urgent.isNotEmpty()) {
                            item {
                                StylishSectionTitle("まもなく期限")
                            }
                            item {
                                DeadlineList(items = urgent)
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        if (upcoming.isNotEmpty()) {
                            item {
                                StylishSectionTitle("今後の予定")
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
        "期限切れ（${-item.deadline.daysRemaining}日超過）"
    } else {
        "あと${item.deadline.daysRemaining}日"
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        tonalElevation = 4.dp,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "最重要",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${item.vehicleName} / ${item.deadline.label}",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${item.deadline.date} （$daysText）",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun DeadlineList(items: List<VehicleDeadlineItem>) {
    StylishConnectedListItemColumn(
        spacing = 4.dp,
        items = items.map { item ->
            val daysText = if (item.deadline.isExpired) {
                "期限切れ（${-item.deadline.daysRemaining}日超過）"
            } else {
                "あと${item.deadline.daysRemaining}日"
            }
            StylishConnectedListItem(
                headline = "${item.vehicleName} / ${item.deadline.label}",
                supportingText = "${item.deadline.date} （$daysText）",
                onClick = {},
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
