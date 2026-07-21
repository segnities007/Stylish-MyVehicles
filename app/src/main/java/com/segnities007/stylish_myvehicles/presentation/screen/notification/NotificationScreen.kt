package com.segnities007.stylish_myvehicles.presentation.screen.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishBottomBar
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishHeader
import com.segnities007.stylish_myvehicles.presentation.components.organisms.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    onAddRecord: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "通知はありません",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            StylishBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                onNavigateToHome = onNavigateToHome,
                onAddRecord = onAddRecord,
                onNavigateToNotifications = {},
                scrollState = listState,
            )
        }
    }
}

@Preview(name = "NotificationScreen", showBackground = true, widthDp = 393)
@Composable
private fun NotificationScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            NotificationScreen(
                onNavigateBack = {},
                onAddRecord = {},
                onNavigateToHome = {},
            )
        }
    }
}
