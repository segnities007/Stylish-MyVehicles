package com.segnities007.stylish_myvehicles.data.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class ReceiptData(
    val volume: String? = null,
    val amount: String? = null,
    val unitPrice: String? = null,
    val odometer: String? = null,
)

object ReceiptScanner {
    private val recognizer by lazy {
        TextRecognition.getClient(
            JapaneseTextRecognizerOptions.Builder()
                .build()
        )
    }

    suspend fun scan(context: Context, uri: Uri): ReceiptData {
        val image = InputImage.fromFilePath(context, uri)
        val text = suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result -> cont.resume(result.text) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        return parseReceipt(text)
    }

    internal fun parseReceipt(text: String): ReceiptData {
        val lines = text.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val fullText = lines.joinToString("\n")

        // 給油量: "32.5L", "32.50ﾘｯﾄﾙ", "給油量 32.5"
        val volume = Regex("""(\d+\.\d+)\s*[Llﾘｯﾄﾙ]""")
            .find(fullText)?.groupValues?.get(1)
            ?: Regex("""[給油量数量]\s*(\d+\.\d+)""")
                .find(fullText)?.groupValues?.get(1)

        // 金額: "¥5,688", "5688円", "合計 5,688"
        val amount = Regex("""[¥￥]\s*([\d,]+)""")
            .find(fullText)?.groupValues?.get(1)
            ?.replace(",", "")
            ?: Regex("""([\d,]+)\s*円""")
                .find(fullText)?.groupValues?.get(1)
                ?.replace(",", "")
            ?: Regex("""[合計金額]\s*[¥￥]?\s*([\d,]+)""")
                .find(fullText)?.groupValues?.get(1)
                ?.replace(",", "")

        // 単価: "175円/L", "175.0円/ﾘｯﾄﾙ"
        val unitPrice = Regex("""(\d+(?:\.\d+)?)\s*円\s*/\s*[Llﾘｯﾄﾙ]""")
            .find(fullText)?.groupValues?.get(1)

        // 走行距離: "45230km", "ODO 45230"
        val odometer = Regex("""(\d{4,7})\s*km""", RegexOption.IGNORE_CASE)
            .find(fullText)?.groupValues?.get(1)
            ?: Regex("""[Oo][Dd][Oo]\s*(\d{4,7})""")
                .find(fullText)?.groupValues?.get(1)

        return ReceiptData(
            volume = volume,
            amount = amount,
            unitPrice = unitPrice,
            odometer = odometer,
        )
    }
}
