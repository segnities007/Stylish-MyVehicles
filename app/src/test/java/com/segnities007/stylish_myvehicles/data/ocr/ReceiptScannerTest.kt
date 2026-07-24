package com.segnities007.stylish_myvehicles.data.ocr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReceiptScannerTest {

    // ── 給油量のパターン ──

    @Test
    fun `給油量_25ドット5Lのパターンを解析できる`() {
        // Arrange（準備）
        val text = "給油量 25.5L"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("25.5", result.volume)
    }

    @Test
    fun `給油量_リットル表記のパターンを解析できる`() {
        // Arrange（準備）
        val text = "32.50ﾘｯﾄﾙ"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("32.50", result.volume)
    }

    @Test
    fun `給油量_小文字lのパターンを解析できる`() {
        // Arrange（準備）
        val text = "28.3l"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("28.3", result.volume)
    }

    @Test
    fun `給油量_数量キーワード付きのパターンを解析できる`() {
        // Arrange（準備）
        val text = "数量 30.25"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("30.25", result.volume)
    }

    // ── 金額のパターン ──

    @Test
    fun `金額_円記号付きカンマありのパターンを解析できる`() {
        // Arrange（準備）
        val text = "¥3,500"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("3500", result.amount)
    }

    @Test
    fun `金額_円記号付きカンマなしのパターンを解析できる`() {
        // Arrange（準備）
        val text = "¥3500"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("3500", result.amount)
    }

    @Test
    fun `金額_円 suffixのパターンを解析できる`() {
        // Arrange（準備）
        val text = "5,688円"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("5688", result.amount)
    }

    @Test
    fun `金額_合計キーワード付きのパターンを解析できる`() {
        // Arrange（準備）
        val text = "合計 4200"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("4200", result.amount)
    }

    @Test
    fun `金額_全角円記号のパターンを解析できる`() {
        // Arrange（準備）
        val text = "￥6,000"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("6000", result.amount)
    }

    // ── 単価のパターン ──

    @Test
    fun `単価_円毎リットルのパターンを解析できる`() {
        // Arrange（準備）
        val text = "165円/L"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("165", result.unitPrice)
    }

    @Test
    fun `単価_小数付き円毎リットルのパターンを解析できる`() {
        // Arrange（準備）
        val text = "175.0円/L"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("175.0", result.unitPrice)
    }

    @Test
    fun `単価_リットル表記のパターンを解析できる`() {
        // Arrange（準備）
        val text = "168円/ﾘｯﾄﾙ"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("168", result.unitPrice)
    }

    // ── 走行距離のパターン ──

    @Test
    fun `走行距離_km付きのパターンを解析できる`() {
        // Arrange（準備）
        val text = "12345km"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("12345", result.odometer)
    }

    @Test
    fun `走行距離_大文字KM付きのパターンを解析できる`() {
        // Arrange（準備）
        val text = "45230KM"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("45230", result.odometer)
    }

    @Test
    fun `走行距離_ODOキーワード付きのパターンを解析できる`() {
        // Arrange（準備）
        val text = "ODO 45230"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("45230", result.odometer)
    }

    @Test
    fun `走行距離_小文字odoキーワード付きのパターンを解析できる`() {
        // Arrange（準備）
        val text = "odo 98765"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("98765", result.odometer)
    }

    // ── マッチしないテキスト ──

    @Test
    fun `マッチしないテキストは全フィールドnullを返す`() {
        // Arrange（準備）
        val text = "こんにちは\nこれはレシートではありません"

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertNull(result.volume)
        assertNull(result.amount)
        assertNull(result.unitPrice)
        assertNull(result.odometer)
    }

    @Test
    fun `空文字は全フィールドnullを返す`() {
        // Arrange（準備）
        // Act（実行）
        val result = ReceiptScanner.parseReceipt("")

        // Assert（検証）
        assertNull(result.volume)
        assertNull(result.amount)
        assertNull(result.unitPrice)
        assertNull(result.odometer)
    }

    // ── 複数パターン混在 ──

    @Test
    fun `複数のパターンが混在するテキストから全フィールドを解析できる`() {
        // Arrange（準備）
        val text = """
            セルフ給油レシート
            給油量 32.5L
            単価 175円/L
            ¥5,688
            ODO 45230
        """.trimIndent()

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("32.5", result.volume)
        assertEquals("5688", result.amount)
        assertEquals("175", result.unitPrice)
        assertEquals("45230", result.odometer)
    }

    @Test
    fun `金額と燃費が混在する場合最初のマッチが使われる`() {
        // Arrange（準備）
        // 金額行を単価行より前に配置（正規表現は最初のマッチを採用する）
        val text = """
            30.0L
            4,950円
            165円/L
        """.trimIndent()

        // Act（実行）
        val result = ReceiptScanner.parseReceipt(text)

        // Assert（検証）
        assertEquals("30.0", result.volume)
        assertEquals("165", result.unitPrice)
        assertEquals("4950", result.amount)
    }
}
