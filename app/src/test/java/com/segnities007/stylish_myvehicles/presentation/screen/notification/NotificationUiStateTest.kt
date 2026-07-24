package com.segnities007.stylish_myvehicles.presentation.screen.notification

import com.segnities007.stylish_myvehicles.domain.service.DeadlineInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class NotificationUiStateTest {

    // ─── NotificationUiState デフォルト ────────────────────────────────

    @Test
    fun `デフォルト状態ではisLoadingがtrueでdeadlinesが空`() {
        // Arrange（準備）
        // Act（実行）
        val state = NotificationUiState()

        // Assert（検証）
        assertTrue(state.isLoading)
        assertTrue(state.deadlines.isEmpty())
    }

    @Test
    fun `deadlinesに複数車両の期限情報を保持できる`() {
        // Arrange（準備）
        val items = listOf(
            VehicleDeadlineItem(
                vehicleId = 1,
                vehicleName = "プリウス",
                deadline = DeadlineInfo(
                    label = "車検",
                    date = LocalDate.of(2027, 3, 31),
                    daysRemaining = 250,
                ),
            ),
            VehicleDeadlineItem(
                vehicleId = 2,
                vehicleName = "フィット",
                deadline = DeadlineInfo(
                    label = "自賠責",
                    date = LocalDate.of(2026, 8, 1),
                    daysRemaining = 8,
                ),
            ),
        )

        // Act（実行）
        val state = NotificationUiState(isLoading = false, deadlines = items)

        // Assert（検証）
        assertFalse(state.isLoading)
        assertEquals(2, state.deadlines.size)
        assertEquals("プリウス", state.deadlines[0].vehicleName)
        assertEquals("フィット", state.deadlines[1].vehicleName)
    }

    // ─── VehicleDeadlineItem + DeadlineInfo ────────────────────────────

    @Test
    fun `期限超過の場合isExpiredがtrueになる`() {
        // Arrange（準備）
        val item = VehicleDeadlineItem(
            vehicleId = 1,
            vehicleName = "プリウス",
            deadline = DeadlineInfo(
                label = "車検",
                date = LocalDate.of(2026, 7, 1),
                daysRemaining = -23,
            ),
        )

        // Act & Assert（実行・検証）
        assertTrue(item.deadline.isExpired)
        assertTrue(item.deadline.isUrgent) // 期限切れも緊急とみなす
    }

    @Test
    fun `残り30日以下の場合isUrgentがtrueになる`() {
        // Arrange（準備）
        val item = VehicleDeadlineItem(
            vehicleId = 1,
            vehicleName = "プリウス",
            deadline = DeadlineInfo(
                label = "保険",
                date = LocalDate.of(2026, 8, 15),
                daysRemaining = 22,
            ),
        )

        // Act & Assert（実行・検証）
        assertFalse(item.deadline.isExpired)
        assertTrue(item.deadline.isUrgent)
    }

    @Test
    fun `残り31日以上の場合isUrgentがfalseになる`() {
        // Arrange（準備）
        val item = VehicleDeadlineItem(
            vehicleId = 1,
            vehicleName = "プリウス",
            deadline = DeadlineInfo(
                label = "車検",
                date = LocalDate.of(2027, 3, 31),
                daysRemaining = 250,
            ),
        )

        // Act & Assert（実行・検証）
        assertFalse(item.deadline.isExpired)
        assertFalse(item.deadline.isUrgent)
    }

    @Test
    fun `残り0日の場合isUrgentがtrueでisExpiredがfalse`() {
        // Arrange（準備）
        val item = VehicleDeadlineItem(
            vehicleId = 1,
            vehicleName = "プリウス",
            deadline = DeadlineInfo(
                label = "車検",
                date = LocalDate.of(2026, 7, 24),
                daysRemaining = 0,
            ),
        )

        // Act & Assert（実行・検証）
        assertFalse(item.deadline.isExpired)
        assertTrue(item.deadline.isUrgent)
    }
}
