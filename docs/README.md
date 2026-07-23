# Stylish-MyVehicles ドキュメント

車両管理アプリ「Stylish-MyVehicles」の企画・設計ドキュメント。

## ドキュメント一覧

| # | ファイル | 内容 |
|---|---------|------|
| 01 | [01-domain-knowledge.md](01-domain-knowledge.md) | 車両管理のドメイン知識（法的制度、メンテナンス、コスト、ライフサイクル） |
| 02 | [02-existing-apps-research.md](02-existing-apps-research.md) | 既存の車両管理アプリ調査（日本向け・海外向け、機能比較マトリクス） |
| 03 | [vehicle-management-app-research.md](vehicle-management-app-research.md) | 改善点・差別化ポイント・UX設計・MVP提案（詳細版） |
| 04 | [04-requirements.md](04-requirements.md) | 要件定義書（機能要件・非機能要件・画面構成・通知設計） |
| 05 | [05-basic-design.md](05-basic-design.md) | 基本設計書（アーキテクチャ・データモデル・画面設計） |
| 06 | [06-detailed-design.md](06-detailed-design.md) | 詳細設計書 |
| 07 | [07-competitive-analysis-and-gap.md](07-competitive-analysis-and-gap.md) | 競合分析とギャップ |
| 08 | [08-multi-vehicle-domain.md](08-multi-vehicle-domain.md) | 複数車両ドメイン設計 |
| 09 | [09-user-stories.md](09-user-stories.md) | ユーザーストーリー集（全39ストーリー、4ペルソナ） |
| 10 | [10-ux-evaluation.md](10-ux-evaluation.md) | UX達成度評価（実装調査に基づく全ストーリーの評価） |
| 12 | [12-competitive-research-2026.md](12-competitive-research-2026.md) | 2026年競合再調査・加重優劣表・改善戦略・ロードマップ |

## 各ドキュメントの概要

### 01 - ドメイン知識
- 日本の法的制度（車検、自賠責、自動車税、重量税、リサイクル券、車庫証明）
- 定期メンテナンス項目と周期・費用目安
- 車両コスト管理（年間維持費の内訳）
- 車両の基本情報（管理すべき項目）
- 車両のライフサイクル（購入→使用→売却）
- 複数台管理のユースケース
- ドメイン用語集

### 02 - 既存アプリ調査
- 日本向けアプリ（MyTOYOTA+、Honda Total Care、e燃費 等）
- 海外向けアプリ（Car Minder、Drivvo、aCar、Fuelly 等）
- 機能カテゴリ別比較マトリクス
- 料金モデルの比較
- 市場のギャップ分析

### 03 - 改善点と差別化（詳細版）
- 既存アプリの不満点分析（UI/UX、入力、通知、データ管理、日本要件）
- UX/UIベストプラクティス（ダッシュボード設計、入力簡素化、通知設計）
- 差別化の方向性（日本市場特化、AI/ML、OBD-II、家族共有）
- 技術的考慮事項（オフラインファースト、プライバシー、OCR、推奨技術スタック）
- ターゲットユーザーペルソナ（4タイプ）
- MVP提案とフェーズ分け（P0/P1/P2）

### 09 - ユーザーストーリー集
- 4ペルソナ（一般ユーザー/車好き/家族共有/資産管理）
- 全39ストーリー（オンボーディング/車両管理/給油/整備/費用/通知/データ管理/設定）
- 各ストーリーに受け入れ基準（Acceptance Criteria）を定義
- 優先度サマリー（P0/P1/P2）

### 10 - UX達成度評価
- 全ユーザーストーリーを実装コードから静的評価
- 総合達成率: 86%（31達成/5部分達成/0未達成、将来機能除く）
- 強み: 一貫したデザイン、オフラインファースト、日本市場特化、マルチカテゴリ
- 要改善: バリデーションフィードバック、プリセット不十分、写真機能欠如

## 調査日

2026-07-20（初版）/ 2026-07-22（09, 10追加）
