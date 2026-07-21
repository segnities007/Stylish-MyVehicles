package com.segnities007.stylish_mycars.presentation.screen.cost.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_mycars.domain.model.CostCategory
import com.segnities007.stylish_mycars.domain.model.CostRecord
import com.segnities007.stylish_mycars.presentation.components.molecules.BarChartData
import com.segnities007.stylish_mycars.presentation.components.molecules.PieChartData
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedChipRow
import com.segnities007.stylish_mycars.presentation.components.molecules.costCategoryColor
import com.segnities007.stylish_mycars.presentation.components.molecules.models.StylishConnectedChipItem
import com.segnities007.stylish_mycars.presentation.components.organisms.BarChartSection
import com.segnities007.stylish_mycars.presentation.components.organisms.PieChartSection
import com.segnities007.stylish_mycars.presentation.screen.cost.CostListIntent
import com.segnities007.stylish_mycars.presentation.screen.cost.CostListUiState
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme
import java.time.LocalDate

/** 費用サマリー（合計・カテゴリフィルタ・円グラフ・月次棒グラフ）。 */
@Composable
fun CostSummarySection(
    state: CostListUiState,
    onIntent: (CostListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        StylishConnectedChipRow(
            items = buildList {
                add(
                    StylishConnectedChipItem(
                        label = "すべて",
                        onClick = { onIntent(CostListIntent.SelectCategory(null)) },
                        selected = state.selectedCategory == null,
                    )
                )
                CostCategory.entries.forEach { category ->
                    add(
                        StylishConnectedChipItem(
                            label = category.label,
                            onClick = { onIntent(CostListIntent.SelectCategory(category)) },
                            selected = state.selectedCategory == category,
                        )
                    )
                }
            },
        )
        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    "今月の費用",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${String.format("%,d", state.monthlyTotal)}円",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "合計（表示中）",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${String.format("%,d", state.totalAmount)}円",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        val categoryTotals = CostCategory.entries.mapNotNull { cat ->
            val total = state.records
                .filter { it.category == cat }
                .sumOf { it.amount }
            if (total > 0) PieChartData(cat.label, total.toFloat(), costCategoryColor(cat.ordinal))
            else null
        }
        PieChartSection(
            title = "費用カテゴリ",
            data = categoryTotals,
        )
        Spacer(Modifier.height(16.dp))

        val now = LocalDate.now()
        val monthlyData = (5 downTo 0).map { monthsAgo ->
            val month = now.minusMonths(monthsAgo.toLong())
            val total = state.records
                .filter { it.date.year == month.year && it.date.month == month.month }
                .sumOf { it.amount }
            BarChartData("${month.monthValue}月", total.toFloat())
        }
        BarChartSection(
            title = "月次費用",
            data = monthlyData,
        )
    }
}

@Preview(name = "Cost summary section", showBackground = true, widthDp = 393)
@Composable
private fun CostSummarySectionPreview() {
    StylishMyCarsTheme {
        Surface(Modifier.padding(20.dp)) {
            CostSummarySection(
                state = CostListUiState(
                    records = listOf(
                        CostRecord(
                            vehicleId = 1,
                            date = LocalDate.now(),
                            category = CostCategory.FUEL,
                            title = "給油",
                            amount = 50000
                        ),
                        CostRecord(
                            vehicleId = 1,
                            date = LocalDate.now(),
                            category = CostCategory.INSURANCE,
                            title = "保険",
                            amount = 30000
                        ),
                        CostRecord(
                            vehicleId = 1,
                            date = LocalDate.now(),
                            category = CostCategory.TAX,
                            title = "税金",
                            amount = 45000
                        ),
                    ),
                ),
                onIntent = {},
            )
        }
    }
}
