# StylishUI

StylishUIは、Stylish My Vehiclesで培った視覚言語と操作原則を、
アプリのドメインから独立して再利用するためのAndroid Composeデザインシステムです。

## 原則

1. **Semantic first**  
   色や寸法を画面へ直書きせず、役割を表すトークンを利用する。
2. **State is explicit**  
   enabled・selected・actionable・loadingなどの状態をAPIで明示する。
   空のクリックコールバックで操作可能に見せない。
3. **Accessibility by construction**  
   最小タップ領域、意味的なRole、content description、十分なコントラストを
   コンポーネント側の既定値として保証する。
4. **One-way dependencies**  
   tokens → foundations → components → patterns の向きだけを許可する。
5. **No product domain**  
   Vehicle、Fuel、Maintenance、Navigation、Room、Koinなどへ依存しない。
6. **System adaptive**  
   Dynamic Color、light/dark、font scale、window insetsを尊重する。
7. **Previewable and testable**  
   公開コンポーネントは主要状態のPreviewを持ち、geometryと状態判定は単体テスト可能にする。

## レイヤー

```text
tokens
  └─ 色・寸法・タイポグラフィ・モーション・Elevation
foundation
  └─ Connected geometry、Outline、Theme composition locals
components
  ├─ atoms
  └─ molecules
patterns
  └─ Header、Scaffold、Dialog、Chart sectionなどの汎用構成
```

アプリ固有の`AddRecordDialog`、`VehicleInfoSection`、`VehicleDeadlineSection`などは
StylishUIを組み合わせる利用側のUIであり、このモジュールには含めません。

## 現在の公開トークン

`StylishDimensions`が以下を一元管理します。

- Connected要素間のspacing
- Outline幅
- 通常操作面とFloating面のElevation
- Connected／joined／Floatingのcorner radius

## 公開API

- `theme`
  - `StylishTheme`
  - `StylishLightColorScheme` / `StylishDarkColorScheme`
  - `StylishTypography`
  - `MaterialTheme.stylishComponentColors`
  - `MaterialTheme.stylishChartColors`
- `foundation`
  - `ConnectedCorners` / `ConnectedEdges`
  - `connectedShape` / `connectedOutline`
  - Row・Column・Grid用のConnected geometry
- `components.atoms`
  - `StylishFab`
  - `StylishIconButton`
  - `StylishRoundedIconButton`
- `components.molecules`
  - Connected Cardの単体・Row・Column・Grid
  - Connected ButtonのRow・Column・Grid
  - `StylishConnectedChipRow`
  - `StylishConnectedListItemColumn`
  - `StylishConnectedSegmentedControl`
  - Dialog・DatePicker・FormTextField・EmptyState
- `components.charts`
  - Bar・Line・Pie chart primitivesと表示用データモデル
- `components.patterns`
  - `StylishHeader`
  - `StylishScaffold`
  - `StylishPageContent`
  - `StylishSectionTitle`
  - Bar・Line chart section

## 利用例

```kotlin
StylishTheme(darkTheme = isSystemInDarkTheme()) {
    StylishConnectedCardGrid(
        items = listOf(
            StylishConnectedCardItem(
                title = "操作できる項目",
                onClick = { /* action */ },
            ),
            StylishConnectedCardItem(
                title = "表示専用の項目",
                onClick = null,
            ),
        ),
        columns = 2,
    )
}
```

クリック処理のない要素へ空ラムダを渡さず、`null`を指定します。
Card・Button・Chip・ListItemは同じactionable判定を使い、
非actionable時にはクリック処理とElevationを付与しません。

ホストアプリ側がDynamic Colorを使う場合は、生成した`ColorScheme`を
`StylishTheme(colorScheme = ...)`へ渡します。設定保存、端末テーマの選択、
Windowのsystem bar制御はデザインシステムではなくホストアプリの責務です。

日付選択や確認ダイアログのボタン文言もライブラリに固定せず、
ホストアプリが渡します。これにより文字列リソースとローカライズは
利用アプリ側で管理できます。

## 依存関係

```kotlin
dependencies {
    implementation(project(":stylish-ui"))
}
```

`stylish-ui`はアプリのdomain、database、navigation、DIへ依存しません。
車両情報や記録追加などのproduct-specific UIはアプリ側で公開コンポーネントを
組み合わせて構築します。依存方向を逆転させないでください。

Light・Darkとactionable・read-only・disabledの代表状態は
`StylishComponentCatalog`のPreviewでまとめて確認できます。

## 移行方針

移行中もアプリを常にビルド可能に保ちます。

1. tokens
2. Connected geometryとTheme
3. atoms
4. molecules
5. 汎用patterns
6. package namespaceの`com.segnities007.stylishui`への統一

各段階で`:stylish-ui:testDebugUnitTest`と`:app:assembleDebug`を通し、
ライブラリからアプリへの逆依存がないことを確認します。
