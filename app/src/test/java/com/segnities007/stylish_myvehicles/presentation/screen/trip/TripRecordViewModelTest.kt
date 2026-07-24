package com.segnities007.stylish_myvehicles.presentation.screen.trip

import app.cash.turbine.test
import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import com.segnities007.stylish_myvehicles.domain.model.TripRecord
import com.segnities007.stylish_myvehicles.test.FakeTripRecordRepository
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripRecordViewModelTest {

    private lateinit var tripRepo: FakeTripRecordRepository

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        tripRepo = FakeTripRecordRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(vehicleId: Long = 1L) =
        TripRecordViewModel(vehicleId, tripRepo)

    @Test
    fun `初期状態でトリップ記録が読み込まれる`() = runTest {
        // Arrange（準備）
        tripRepo.seed(
            TripRecord(
                id = 1, vehicleId = 1, title = "ドライブ",
                startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
                endedAt = LocalDateTime.of(2026, 7, 1, 12, 0),
                distanceMeters = 50000,
            ),
            TripRecord(
                id = 2, vehicleId = 1, title = "買い物",
                purpose = TripPurpose.SHOPPING,
                startedAt = LocalDateTime.of(2026, 7, 5, 14, 0),
                endedAt = LocalDateTime.of(2026, 7, 5, 15, 30),
                distanceMeters = 10000,
            ),
        )

        // Act（実行）
        val vm = createViewModel()

        // Assert（検証）
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.records.size)
            assertEquals("ドライブ", state.records[0].title)
            assertEquals("買い物", state.records[1].title)
        }
    }

    @Test
    fun `トリップを保存するとリポジトリに追加される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)
        vm.accept(TripRecordIntent.TitleChanged("通勤"))
        vm.accept(TripRecordIntent.PurposeChanged(TripPurpose.COMMUTE))
        vm.accept(TripRecordIntent.DateChanged(LocalDate.of(2026, 7, 20)))
        vm.accept(TripRecordIntent.StartTimeChanged("08:00"))
        vm.accept(TripRecordIntent.EndTimeChanged("09:00"))
        vm.accept(TripRecordIntent.DistanceChanged("15.0"))

        // Act（実行）
        vm.accept(TripRecordIntent.Save)

        // Assert（検証）
        val state = vm.uiState.value
        assertFalse(state.isInputDialogOpen)
        assertEquals(1, state.records.size)
        val saved = state.records.first()
        assertEquals("通勤", saved.title)
        assertEquals(TripPurpose.COMMUTE, saved.purpose)
        assertEquals(15000L, saved.distanceMeters)
    }

    @Test
    fun `日付またぎのトリップで終了日が翌日になる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)
        vm.accept(TripRecordIntent.TitleChanged("夜間ドライブ"))
        vm.accept(TripRecordIntent.DateChanged(LocalDate.of(2026, 7, 24)))
        vm.accept(TripRecordIntent.StartTimeChanged("23:00"))
        vm.accept(TripRecordIntent.EndTimeChanged("01:00"))
        vm.accept(TripRecordIntent.DistanceChanged("80.0"))

        // Act（実行）
        vm.accept(TripRecordIntent.Save)

        // Assert（検証）
        val saved = vm.uiState.value.records.first()
        assertEquals(LocalDateTime.of(2026, 7, 24, 23, 0), saved.startedAt)
        // 終了時刻が開始時刻より前なので翌日になる
        assertEquals(LocalDateTime.of(2026, 7, 25, 1, 0), saved.endedAt)
    }

    @Test
    fun `km入力がメートルに正しく変換される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)
        vm.accept(TripRecordIntent.TitleChanged("テスト"))
        vm.accept(TripRecordIntent.DateChanged(LocalDate.of(2026, 7, 20)))
        vm.accept(TripRecordIntent.StartTimeChanged("10:00"))
        vm.accept(TripRecordIntent.EndTimeChanged("11:00"))
        vm.accept(TripRecordIntent.DistanceChanged("12.5"))

        // Act（実行）
        vm.accept(TripRecordIntent.Save)

        // Assert（検証）
        // 12.5 km = 12500 m
        val saved = vm.uiState.value.records.first()
        assertEquals(12500L, saved.distanceMeters)
    }

    @Test
    fun `削除確認で記録が削除される`() = runTest {
        // Arrange（準備）
        tripRepo.seed(
            TripRecord(
                id = 1, vehicleId = 1, title = "削除対象",
                startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
                endedAt = LocalDateTime.of(2026, 7, 1, 12, 0),
                distanceMeters = 50000,
            ),
        )
        val vm = createViewModel()
        vm.accept(TripRecordIntent.RequestDelete(1))
        assertEquals(1L, vm.uiState.value.deletingRecordId)

        // Act（実行）
        vm.accept(TripRecordIntent.ConfirmDelete)

        // Assert（検証）
        assertTrue(vm.uiState.value.records.isEmpty())
        assertNull(vm.uiState.value.deletingRecordId)
    }

    @Test
    fun `編集ダイアログを開くと既存レコードの値が入力欄に反映される`() = runTest {
        // Arrange（準備）
        tripRepo.seed(
            TripRecord(
                id = 1, vehicleId = 1, title = "通勤",
                purpose = TripPurpose.COMMUTE,
                startedAt = LocalDateTime.of(2026, 7, 10, 8, 30),
                endedAt = LocalDateTime.of(2026, 7, 10, 9, 15),
                distanceMeters = 15000,
                startOdometer = 10000,
                endOdometer = 10015,
            ),
        )
        val vm = createViewModel()

        // Act（実行）
        vm.accept(TripRecordIntent.Edit(recordId = 1))

        // Assert（検証）
        val state = vm.uiState.value
        assertTrue(state.isInputDialogOpen)
        assertEquals(1L, state.editingRecordId)
        assertEquals("通勤", state.inputTitle)
        assertEquals(TripPurpose.COMMUTE, state.inputPurpose)
        assertEquals("10000", state.inputStartOdometer)
        assertEquals("10015", state.inputEndOdometer)
        assertEquals(LocalDate.of(2026, 7, 10), state.inputDate)
        assertEquals("08:30", state.inputStartTime)
        assertEquals("09:15", state.inputEndTime)
        assertEquals("15.0", state.inputDistanceKm)
    }

    @Test
    fun `編集モードで保存するとレコードが更新される`() = runTest {
        // Arrange（準備）
        tripRepo.seed(
            TripRecord(
                id = 1, vehicleId = 1, title = "通勤",
                purpose = TripPurpose.COMMUTE,
                startedAt = LocalDateTime.of(2026, 7, 10, 8, 0),
                endedAt = LocalDateTime.of(2026, 7, 10, 9, 0),
                distanceMeters = 15000,
            ),
        )
        val vm = createViewModel()
        vm.accept(TripRecordIntent.Edit(recordId = 1))
        vm.accept(TripRecordIntent.TitleChanged("通学"))
        vm.accept(TripRecordIntent.PurposeChanged(TripPurpose.OTHER))
        vm.accept(TripRecordIntent.DistanceChanged("20.0"))

        // Act（実行）
        vm.accept(TripRecordIntent.Save)

        // Assert（検証）
        val updated = vm.uiState.value.records.find { it.id == 1L }
        assertEquals("通学", updated?.title)
        assertEquals(TripPurpose.OTHER, updated?.purpose)
        assertEquals(20000L, updated?.distanceMeters)
        assertFalse(vm.uiState.value.isInputDialogOpen)
    }

    @Test
    fun `削除ターゲットがない状態でConfirmDeleteしても何も起きない`() = runTest {
        // Arrange（準備）
        tripRepo.seed(
            TripRecord(
                id = 1, vehicleId = 1, title = "テスト",
                startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
                endedAt = LocalDateTime.of(2026, 7, 1, 12, 0),
                distanceMeters = 50000,
            ),
        )
        val vm = createViewModel()

        // Act（実行）— deletingRecordId が null のまま ConfirmDelete
        vm.accept(TripRecordIntent.ConfirmDelete)

        // Assert（検証）— レコードは削除されない
        assertEquals(1, vm.uiState.value.records.size)
    }

    @Test
    fun `DismissDeleteで削除確認がキャンセルされる`() = runTest {
        // Arrange（準備）
        tripRepo.seed(
            TripRecord(
                id = 1, vehicleId = 1, title = "テスト",
                startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
                endedAt = LocalDateTime.of(2026, 7, 1, 12, 0),
                distanceMeters = 50000,
            ),
        )
        val vm = createViewModel()
        vm.accept(TripRecordIntent.RequestDelete(1))
        assertEquals(1L, vm.uiState.value.deletingRecordId)

        // Act（実行）
        vm.accept(TripRecordIntent.DismissDelete)

        // Assert（検証）
        assertNull(vm.uiState.value.deletingRecordId)
        assertEquals(1, vm.uiState.value.records.size)
    }

    @Test
    fun `DismissEditで編集ダイアログが閉じる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)
        assertTrue(vm.uiState.value.isInputDialogOpen)

        // Act（実行）
        vm.accept(TripRecordIntent.DismissEdit)

        // Assert（検証）
        assertFalse(vm.uiState.value.isInputDialogOpen)
        assertNull(vm.uiState.value.editingRecordId)
    }

    @Test
    fun `Purpose変更が反映される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)

        // Act（実行）
        vm.accept(TripRecordIntent.PurposeChanged(TripPurpose.TRAVEL))

        // Assert（検証）
        assertEquals(TripPurpose.TRAVEL, vm.uiState.value.inputPurpose)
    }

    @Test
    fun `録画中のレコードは削除できない`() = runTest {
        // Arrange（準備）
        tripRepo.seed(
            TripRecord(
                id = 1, vehicleId = 1, title = "録画中",
                startedAt = LocalDateTime.of(2026, 7, 1, 9, 0),
                endedAt = null, // 録画中
                distanceMeters = 0,
            ),
        )
        val vm = createViewModel()
        vm.accept(TripRecordIntent.RequestDelete(1))

        // Act（実行）
        vm.accept(TripRecordIntent.ConfirmDelete)

        // Assert（検証）— 録画中なので削除されない
        assertEquals(1, vm.uiState.value.records.size)
    }

    @Test
    fun `canSaveは時刻と距離が不正な場合falseになる`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)

        // Act（実行）— 距離が空
        vm.accept(TripRecordIntent.StartTimeChanged("10:00"))
        vm.accept(TripRecordIntent.EndTimeChanged("11:00"))

        // Assert（検証）
        assertFalse(vm.uiState.value.canSave)
    }

    @Test
    fun `オドメーター入力は整数のみ正規化される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)

        // Act（実行）
        vm.accept(TripRecordIntent.StartOdometerChanged("123abc"))
        vm.accept(TripRecordIntent.EndOdometerChanged("456def"))

        // Assert（検証）
        assertEquals("123", vm.uiState.value.inputStartOdometer)
        assertEquals("456", vm.uiState.value.inputEndOdometer)
    }

    @Test
    fun `時刻入力は数字とコロンのみ許可される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)

        // Act（実行）
        vm.accept(TripRecordIntent.StartTimeChanged("ab10:30cd"))

        // Assert（検証）
        assertEquals("10:30", vm.uiState.value.inputStartTime)
    }

    @Test
    fun `距離入力は小数正規化される`() = runTest {
        // Arrange（準備）
        val vm = createViewModel()
        vm.accept(TripRecordIntent.OpenManualAdd)

        // Act（実行）— 2つ目のドットは無視され、数字は連結される
        vm.accept(TripRecordIntent.DistanceChanged("12.5.3"))

        // Assert（検証）
        assertEquals("12.53", vm.uiState.value.inputDistanceKm)
    }
}
