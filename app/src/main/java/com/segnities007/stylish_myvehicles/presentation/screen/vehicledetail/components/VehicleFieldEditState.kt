package com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.components

import androidx.compose.runtime.Stable
import com.segnities007.stylish_myvehicles.domain.model.VehicleCategory
import java.time.LocalDate

/**
 * 車両フィールド編集ダイアログの入力値。
 *
 * 単一の情報源はVehicleDetailViewModelのUiStateであり、このクラスは
 * 値を束ねて引数を減らすためだけのもの。規約はAGENTS.mdを参照。
 */
@Stable
data class VehicleFieldEditValue(
    val inputText: String,
    val inputDate: LocalDate?,
    val inputCategory: VehicleCategory,
)

/**
 * 車両フィールド編集ダイアログで発生するイベント（外部副作用）のまとまり。
 */
interface VehicleFieldEditActions {
    /** テキスト入力が変更された。 */
    fun onTextChanged(text: String)

    /** 日付入力が変更された。 */
    fun onDateChanged(date: LocalDate?)

    /** カテゴリ選択が変更された。 */
    fun onCategoryChanged(category: VehicleCategory)

    /** 編集を確定して保存する。 */
    fun save()

    /** 編集を破棄してダイアログを閉じる。 */
    fun dismiss()
}
