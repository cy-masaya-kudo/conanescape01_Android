# コナン脱出第1弾

コナン脱出ゲームの第1弾アプリです。 

脱出第2〜3弾のコードとほぼ共通です。

署名ファイルも脱出第2〜3弾のものと共通で使用しています。

## 依存関係

### ローカルモジュール

|モジュール|概要|
|--|--|
|app|アプリの本体モジュール|
| CYCompliance | 親権者同意画面|
| libBilling | 課金ライブラリ(IAB v3)|
| libPoint | ポイントAPIモジュール|
|escapeEngineJava|脱出ゲームエンジン(pure javaライブラリ)|
|POPgate|暗号化|
|cy_cybirdid|UUID生成サポート(旧CybirdUtilityのサルベージ用)|
|cy_gencydid|UUID生成サポート|
|cy_gcm|Cybird共通基盤GCM。※2019年4月終了だが、クライアントの受信は引き続き可能|
|gcm_sub_toPUSH|Cybird共通基盤GCM用の便利クラス|

### 外部ライブラリ

|ライブラリ|概要|
|--|--|
|play-services-analytics|GoogleAnalytics用。※2019年10月サービス終了。クライアントSDKは動くが、計上されなくなる|
|play-services-gcm|GCM。2019年4月終了だが、クライアントの受信は引き続き可能|

## build variantについて

|build variant|サーバー向き先|用途|
|--|--|--|
|developDebug|開発|開発中のデバッグ用。当たり判定表示のフラグなどが実行中に変更可能等、デバッグ用メニューあり|
|developEnterprise|開発|デバッグメニュー無しでの動作確認用|
|developRelease|開発|releaseビルドでの、ProGuard適用状態での動作確認用|
|productDebug|本番|本番サーバーでの動作確認用。本番向きのみで不具合が起きたとき等に使用|
|productRelease|本番|Playストアアップ用|

## リリース

### リリース前の確認事項

- 各種バージョン番号
  - デバッグ用になっていないか
  - 前回リリースより上げてあるか


### リリースビルド

- ビルド

	下記コマンドを実行するとoutフォルダにapkが生成されます。
	
	```
	$ ./gradlew publish_product_release
	```

- svnコミット
  - proguard用の生成物のコミット漏れ注意

### タグ付け

リリース後は、svnにtagsを作成するのを忘れないでください。

タグの命名規則は以下の通りです。

`conanescape01_Android_Version{$versionName}`

