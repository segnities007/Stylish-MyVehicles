# AGENTS.md — StylishMyVehicles コーディング規約

このファイルは AI エージェントおよび全貢献者が従うべき強制ルールを定義する。

## ファイル配置

### スクリーンファイルの配置場所
- 各 Screen Composable は **対応する機能ディレクトリ** に配置する
  - 例: `FuelRecordsScreen.kt` → `presentation/screen/fuel/`
  - 例: `CostRecordsScreen.kt` → `presentation/screen/cost/`
- **共有コンポーネント**（複数スクリーンが使う ViewModel, UiState, Intent, Effect, Layout）のみ `records/` 等の共有ディレクトリに配置可
- 新しいスクリーンを追加する際は、既存のディレクトリ構造に従う

### ディレクトリ構造の原則
```
presentation/screen/{feature}/
├── {Feature}Screen.kt          # Screen Composable（@Preview 必須）
├── {Feature}ViewModel.kt       # ViewModel
├── {Feature}Intent.kt          # Intent sealed interface
├── {Feature}UiState.kt         # UiState data class
├── {Feature}Effect.kt          # Effect sealed interface（必要な場合）
└── components/                 # その機能専用のサブコンポーネント
    └── {Feature}InputDialog.kt # （@Preview 必須）
```

## 一ファイル一関心（Single Concern per File）

### 強制ルール
- **1ファイル = 1つの主要な宣言**（class / interface / object）
- 以下の例外を除き、複数のトップレベル宣言を1ファイルに含めてはならない

### 例外（同一ファイルに含めてよい）
- `sealed interface` + その `data class` / `data object` 実装群（Intent, Effect 等）
- `data class` + その `companion object`
- 密結合な小規模ヘルパー関数（`private` または `internal` で同一ファイル内からのみ使用）

### 違反の例
- ❌ ViewModel + Effect sealed interface が同一ファイル
- ❌ 無関係な data class が複数同一ファイル
- ❌ Screen Composable + 別の Screen Composable が同一ファイル

## @Preview の義務

### 強制ルール
- **全ての Screen Composable ファイル** に最低1つの `@Preview` 関数を含める
- **全ての Dialog Composable ファイル** に最低1つの `@Preview` 関数を含める
- **全ての公開コンポーネント**（`presentation/components/` 配下）に最低1つの `@Preview` 関数を含める
- Preview 関数は `private` で命名は `{ComposableName}Preview`

### Preview の書き方
```kotlin
@Preview(showBackground = true, widthDp = 393)
@Composable
private fun MyScreenPreview() {
    StylishMyVehiclesTheme {
        // ダミーデータで Composable を描画
    }
}
```

## テスト規約

### 命名
- テスト名は **日本語** で backtick 構文を使う
  - 例: `` @Test fun `満タン給油で燃費が計算される`() ``

### 構造
- **AAA パターン** を日本語コメントで明示する
```kotlin
@Test
fun `給油レコードを保存すると費用レコードも自動作成される`() {
    // Arrange（準備）
    val repository = FakeFuelRecordRepository()
    ...

    // Act（実行）
    viewModel.accept(FuelRecordIntent.Save)

    // Assert（検証）
    assertEquals(expected, actual)
}
```

### ViewModel テスト
- Fake Repository（`test/FakeRepositories.kt`）を使う
- `UnconfinedTestDispatcher` + `Dispatchers.setMain` / `resetMain`
- Flow の検証には Turbine（`app.cash.turbine.test`）を使う

### カバレッジ
- JaCoCo で **LINE カバレッジ 90% 以上** を強制（`jacocoVerify`）
- 新規コードはテストとセットで PR に含める
- バグ修正時は再現テストを先に書く

## MVI パターン

- 全画面は Intent / UiState / Effect / ViewModel の4要素で構成する
- UiState は **immutable**（`val` のみ、`@Immutable` 付与）
- Intent は `sealed interface`、`accept(intent)` で受信
- Effect は one-shot（`Channel`）、UiState に含めない
- ViewModel にドメインロジックを書かない（`domain/service/` に抽出）

## 依存関係

- ViewModel は **Repository インターフェース** に依存する（UseCase ではない）
- UseCase は **クロス集約のオーケストレーション**（複数 Repository の調整）の場合のみ作成
- 1 Repository への単純委譲 UseCase は作成しない（YAGNI）
- ドメインモデル（`domain/model/`）は純粋なデータ保持。サービスに依存しない

## Compose 規約

- `collectAsState()` ではなく **`collectAsStateWithLifecycle()`** を使う
- Scaffold の `innerPadding` は必ずコンテンツに適用する
- `LazyColumn` / `LazyRow` の items には `key` を指定する
- Composable 関数の第一引数は `Modifier`（任意引数として）
