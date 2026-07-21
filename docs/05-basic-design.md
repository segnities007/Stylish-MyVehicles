# 基本設計書 — Stylish MyCars

> 作成日: 2026-07-20
> 参照: [要件定義書](04-requirements.md)

---

## 1. システムアーキテクチャ

### 1.1 レイヤー構成

Clean Architecture + MVVM（MVI風）を採用。StylishMemoと同一のアーキテクチャ。

```
┌─────────────────────────────────────────┐
│  App 層                                  │
│  StylishMyVehiclesApp / MainActivity / DI    │
├─────────────────────────────────────────┤
│  Presentation 層                         │
│  Screen / ViewModel / UiState / Intent   │
│  Components (atoms / molecules / organisms)│
│  Navigation / Theme                      │
├─────────────────────────────────────────┤
│  Domain 層                               │
│  Model / Repository(I/F) / UseCase       │
├─────────────────────────────────────────┤
│  Data 層                                 │
│  Room (Entity / DAO / Database)          │
│  RepositoryImpl / Mapper                 │
└─────────────────────────────────────────┘
```

### 1.2 依存方向

```
Presentation ──> Domain <── Data
       │                     │
       └──────── App ────────┘
```

- Domain層はどの層にも依存しない（Pure Kotlin）
- Presentation層はDomain層のみに依存
- Data層はDomain層のRepositoryインターフェースを実装
- App層がDI（Koin）で全層を結合

### 1.3 データフロー（単方向）

```
User Action
    ↓
UiIntent (sealed interface)
    ↓
ViewModel.accept(intent)
    ↓
UseCase ──> Repository ──> Room DAO
    ↑
    └── Flow<DomainModel> ──> ViewModel ──> UiState ──> UI Recomposition
```

---

## 2. データモデル

### 2.1 ER図

```
┌──────────────┐       ┌──────────────────┐
│   Vehicle    │ 1───* │   FuelRecord     │
│              │       │                  │
│ id (PK)      │       │ id (PK)          │
│ maker        │       │ vehicleId (FK)   │
│ name         │       │ date             │
│ grade        │       │ odometer         │
│ year         │       │ volume           │
│ modelCode    │       │ amount           │
│ plateNumber  │       │ unitPrice        │
│ vin          │       │ fuelEconomy      │
│ displacement │       │ isFullTank       │
│ weight       │       │ memo             │
│ color        │       │ createdAt        │
│ firstRegDate │       └──────────────────┘
│ inspectionExp│
│ jibaiExp     │       ┌──────────────────┐
│ insuranceExp │ 1───* │MaintenanceRecord │
│ insuranceCo  │       │                  │
│ insuranceRank│       │ id (PK)          │
│ taxPaid      │       │ vehicleId (FK)   │
│ photoUri     │       │ date             │
│ memo         │       │ odometer         │
│ createdAt    │       │ category         │
│ updatedAt    │       │ title            │
└──────────────┘       │ cost             │
                       │ shopName         │
                       │ memo             │
                       │ createdAt        │
                       └──────────────────┘

┌──────────────────┐
│  CostRecord      │  ← 費用の汎用記録（保険料、税金、その他）
│                  │
│ id (PK)          │
│ vehicleId (FK)   │
│ date             │
│ category         │  (fuel / maintenance / insurance / tax / parking / other)
│ title            │
│ amount           │
│ memo             │
│ createdAt        │
└──────────────────┘
```

### 2.2 ドメインモデル（Domain層）

```kotlin
// Vehicle.kt
data class Vehicle(
    val id: Long = 0,
    val maker: String,
    val name: String,
    val grade: String = "",
    val year: Int? = null,
    val modelCode: String = "",
    val plateNumber: String = "",
    val vin: String = "",
    val displacement: Int? = null,   // cc
    val weight: Int? = null,         // kg
    val color: String = "",
    val firstRegistrationDate: LocalDate? = null,
    val inspectionExpiry: LocalDate? = null,
    val jibaiExpiry: LocalDate? = null,
    val insuranceExpiry: LocalDate? = null,
    val insuranceCompany: String = "",
    val insuranceRank: Int? = null,
    val taxPaid: Boolean = false,
    val photoUri: String? = null,
    val memo: String = "",
)

// FuelRecord.kt
data class FuelRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int,
    val volume: Double,        // L
    val amount: Int,           // 円
    val unitPrice: Int? = null,// 円/L
    val fuelEconomy: Double? = null, // km/L（計算値）
    val isFullTank: Boolean = true,
    val memo: String = "",
)

// MaintenanceRecord.kt
data class MaintenanceRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int? = null,
    val category: MaintenanceCategory,
    val title: String,
    val cost: Int = 0,
    val shopName: String = "",
    val memo: String = "",
)

enum class MaintenanceCategory {
    OIL,           // エンジンオイル
    OIL_FILTER,    // オイルフィルター
    TIRE,          // タイヤ
    BATTERY,       // バッテリー
    BRAKE,         // ブレーキ
    WIPER,         // ワイパー
    AIR_FILTER,    // エアコンフィルター
    COOLANT,       // 冷却水
    SPARK_PLUG,    // スパークプラグ
    BELT,          // ベルト類
    INSPECTION_12, // 法定12ヶ月点検
    INSPECTION_24, // 法定24ヶ月点検
    SHAKEN,        // 車検
    OTHER,         // その他
}

// CostRecord.kt
data class CostRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val category: CostCategory,
    val title: String,
    val amount: Int,
    val memo: String = "",
)

enum class CostCategory {
    FUEL,
    MAINTENANCE,
    INSURANCE,
    TAX,
    PARKING,
    OTHER,
}
```

### 2.3 車検満了日の計算ロジック

```
firstRegistrationDate（初度登録年月）から計算:
  初回: firstRegistrationDate + 3年
  2回目以降: 初回満了日 + 2年 × N

例: 初度登録 2022年4月
  → 初回満了: 2025年4月
  → 2回目満了: 2027年4月
  → 3回目満了: 2029年4月
```

### 2.4 燃費計算ロジック

```
満タン法:
  fuelEconomy = (今回ODO - 前回ODO) / 今回給油量

条件:
  - isFullTank == true の場合のみ計算
  - 前回記録が存在しない場合は null
  - 前回が isFullTank == false の場合は null（参考値）
```

### 2.5 自動車税（種別割）の税額計算

```kotlin
fun calculateVehicleTax(displacement: Int): Int = when {
    displacement <= 1000 -> 25_000
    displacement <= 1500 -> 30_500
    displacement <= 2000 -> 36_000
    displacement <= 2500 -> 43_500
    displacement <= 3000 -> 50_000
    displacement <= 3500 -> 57_000
    displacement <= 4000 -> 65_500
    displacement <= 4500 -> 75_500
    displacement <= 6000 -> 87_000
    else -> 110_000
}
// 軽自動車: 10,800円（排気量660cc以下）
```

---

## 3. 画面設計

### 3.1 画面遷移図

```
                    ┌─────────────────┐
                    │ VehicleListScreen│ ← 起動時
                    │ (車両一覧)        │
                    └────┬───────┬────┘
                         │       │
              車両タップ  │       │ FAB（車両追加）
                         ↓       ↓
              ┌──────────────┐  ┌──────────────────┐
              │VehicleDetail │  │ VehicleEditScreen │
              │Screen        │  │ (車両登録・編集)    │
              └──┬───┬───┬──┘  └──────────────────┘
                 │   │   │
     給油セクション│   │   │整備セクション
                 ↓   │   ↓
    ┌────────────┐│   │┌────────────────┐
    │FuelRecord  ││   ││MaintenanceRecord│
    │Screen      ││   ││Screen           │
    └────────────┘│   │└────────────────┘
                  │   │
         費用セクション│
                  ↓
         ┌────────────┐
         │CostList    │
         │(費用一覧)   │
         └────────────┘
```

### 3.2 画面一覧

| 画面 | 役割 | 主要コンポーネント |
|------|------|------------------|
| VehicleListScreen | 車両一覧・ダッシュボード | StylishHeader, StylishConnectedCardColumn, FAB |
| VehicleDetailScreen | 車両の詳細情報と期限管理 | StylishHeader, StylishConnectedListItemColumn, StylishConnectedCardRow |
| VehicleEditScreen | 車両情報の登録・編集 | StylishHeader, テキストフィールド, StylishDialogActions |
| FuelRecordScreen | 給油記録の一覧・入力 | StylishHeader, リスト, StylishDialogSurface（入力ダイアログ） |
| MaintenanceRecordScreen | 整備記録の一覧・入力 | StylishHeader, タイムライン, StylishDialogSurface |
| CostListScreen | 費用の一覧・集計 | StylishHeader, StylishConnectedChipRow（カテゴリフィルタ） |

### 3.3 VehicleListScreen（ダッシュボード）のレイアウト

```
┌─────────────────────────────────────┐
│  StylishHeader: "My Cars"           │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 🚗 プリウス                  │    │  ← StylishConnectedCard
│  │ 車検: 2027/04 (あと9ヶ月) 🟢 │    │
│  │ ODO: 45,230km               │    │
│  └─────────────────────────────┘    │
│  ┌─────────────────────────────┐    │
│  │ 🚗 N-BOX                    │    │
│  │ 車検: 2026/09 (あと2ヶ月) 🟡 │    │
│  │ ODO: 12,100km               │    │
│  └─────────────────────────────┘    │
│                                     │
│                          [+ FAB]    │
└─────────────────────────────────────┘
```

### 3.4 VehicleDetailScreen のレイアウト

```
┌─────────────────────────────────────┐
│  StylishHeader: "プリウス"  [←]     │
├─────────────────────────────────────┤
│  期限管理                            │
│  ┌─────────────────────────────┐    │
│  │ 車検        2027/04  あと9ヶ月│    │  ← StylishConnectedListItemColumn
│  │ 自賠責      2027/04  あと9ヶ月│    │
│  │ 任意保険    2026/11  あと4ヶ月│    │
│  │ 自動車税    納付済み ✓       │    │
│  └─────────────────────────────┘    │
│                                     │
│  最近の記録                          │
│  ┌──────────────┬──────────────┐    │
│  │ 給油 18.5km/L│ 整備 オイル交換│    │  ← StylishConnectedCardRow
│  │ 07/15        │ 07/01        │    │
│  └──────────────┴──────────────┘    │
│                                     │
│  費用サマリー（今月）                 │
│  ┌─────────────────────────────┐    │
│  │ ¥12,340    先月比 -8%        │    │
│  └─────────────────────────────┘    │
└─────────────────────────────────────┘
```

---

## 4. パッケージ構成

```
com.segnities007.stylish_myvehicles/
├── app/
│   ├── di/
│   │   └── AppModule.kt
│   ├── MainActivity.kt
│   └── StylishMyVehiclesApp.kt
├── domain/
│   ├── model/
│   │   ├── Vehicle.kt
│   │   ├── FuelRecord.kt
│   │   ├── MaintenanceRecord.kt
│   │   ├── CostRecord.kt
│   │   ├── MaintenanceCategory.kt
│   │   └── CostCategory.kt
│   ├── repository/
│   │   ├── VehicleRepository.kt
│   │   ├── FuelRecordRepository.kt
│   │   ├── MaintenanceRecordRepository.kt
│   │   └── CostRecordRepository.kt
│   └── usecase/
│       ├── vehicle/
│       │   ├── GetVehiclesUseCase.kt
│       │   ├── GetVehicleUseCase.kt
│       │   ├── InsertVehicleUseCase.kt
│       │   ├── UpdateVehicleUseCase.kt
│       │   └── DeleteVehicleUseCase.kt
│       ├── fuel/
│       │   ├── GetFuelRecordsUseCase.kt
│       │   ├── InsertFuelRecordUseCase.kt
│       │   └── DeleteFuelRecordUseCase.kt
│       ├── maintenance/
│       │   ├── GetMaintenanceRecordsUseCase.kt
│       │   ├── InsertMaintenanceRecordUseCase.kt
│       │   └── DeleteMaintenanceRecordUseCase.kt
│       └── cost/
│           ├── GetCostRecordsUseCase.kt
│           ├── InsertCostRecordUseCase.kt
│           └── DeleteCostRecordUseCase.kt
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   ├── VehicleEntity.kt
│   │   │   ├── FuelRecordEntity.kt
│   │   │   ├── MaintenanceRecordEntity.kt
│   │   │   └── CostRecordEntity.kt
│   │   ├── dao/
│   │   │   ├── VehicleDao.kt
│   │   │   ├── FuelRecordDao.kt
│   │   │   ├── MaintenanceRecordDao.kt
│   │   │   └── CostRecordDao.kt
│   │   ├── AppDatabase.kt
│   │   └── Converters.kt
│   ├── repository/
│   │   ├── VehicleRepositoryImpl.kt
│   │   ├── FuelRecordRepositoryImpl.kt
│   │   ├── MaintenanceRecordRepositoryImpl.kt
│   │   └── CostRecordRepositoryImpl.kt
│   └── mapper/
│       ├── VehicleMapper.kt
│       ├── FuelRecordMapper.kt
│       ├── MaintenanceRecordMapper.kt
│       └── CostRecordMapper.kt
└── presentation/
    ├── components/          ← 移植済み
    │   ├── atoms/
    │   ├── molecules/
    │   └── organisms/
    ├── navigation/          ← 移植済み
    │   ├── AppDestination.kt
    │   └── AppNavigation.kt
    ├── screen/
    │   ├── vehiclelist/
    │   │   ├── VehicleListScreen.kt
    │   │   ├── VehicleListViewModel.kt
    │   │   ├── VehicleListUiState.kt
    │   │   └── VehicleListIntent.kt
    │   ├── vehicledetail/
    │   │   ├── VehicleDetailScreen.kt
    │   │   ├── VehicleDetailViewModel.kt
    │   │   ├── VehicleDetailUiState.kt
    │   │   └── VehicleDetailIntent.kt
    │   ├── vehicleedit/
    │   │   ├── VehicleEditScreen.kt
    │   │   ├── VehicleEditViewModel.kt
    │   │   ├── VehicleEditUiState.kt
    │   │   └── VehicleEditIntent.kt
    │   ├── fuel/
    │   │   ├── FuelRecordScreen.kt
    │   │   ├── FuelRecordViewModel.kt
    │   │   ├── FuelRecordUiState.kt
    │   │   └── FuelRecordIntent.kt
    │   ├── maintenance/
    │   │   ├── MaintenanceRecordScreen.kt
    │   │   ├── MaintenanceRecordViewModel.kt
    │   │   ├── MaintenanceRecordUiState.kt
    │   │   └── MaintenanceRecordIntent.kt
    │   └── cost/
    │       ├── CostListScreen.kt
    │       ├── CostListViewModel.kt
    │       ├── CostListUiState.kt
    │       └── CostListIntent.kt
    └── theme/               ← 移植済み
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 5. 主要技術決定

| 決定事項 | 選択 | 理由 |
|---------|------|------|
| ローカルDB | Room | オフラインファースト、Android標準、Flow対応 |
| DI | Koin 4.0 | StylishMemoと統一、軽量、Compose統合 |
| ナビゲーション | Navigation3 | StylishMemoと統一、型安全、予測的バック |
| 非同期 | Coroutines + Flow | Composeとの親和性、RoomのFlow対応 |
| 日付 | java.time.LocalDate | Domain層でAndroid SDK非依存 |
| 状態管理 | UiState (data class) + Intent (sealed interface) | MVI風、単方向データフロー |
| 画像読み込み | Coil（Phase 2） | Compose対応、軽量 |
| グラフ | Vico or Canvas（Phase 2） | 燃費推移・費用グラフ |

---

## 6. 通知アーキテクチャ

```
WorkManager (PeriodicWorkRequest, 1日1回)
    ↓
NotificationScheduler
    ├── 車検満了日のチェック → 段階的通知
    ├── 自賠責満了日のチェック
    ├── 任意保険満了日のチェック
    ├── 自動車税納付時期のチェック（5月）
    └── メンテナンス目安のチェック（ODO + 期間）
    ↓
NotificationManager (チャネル別)
    ├── vehicle_inspection (車検・自賠責)
    ├── insurance (任意保険)
    ├── tax (税金)
    └── maintenance (整備)
```

- WorkManagerでバックグラウンドチェック（Dozeモード対応）
- 通知チャネルを分離し、ユーザーがチャネル単位で制御可能
- 通知タップ → Deep Link で該当画面に遷移
