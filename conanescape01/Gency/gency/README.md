
# Gency/gency
 各種ユーティリティをまとめたライブラリ  
 - com.gency.aid // ユーザー識別IDを取得する機能を提供する（CybirdUtilityとは無関係）  
 - com.gency.commons // 便利機能いろいろ  
 - com.gency.version // Gencyバージョン定数  


## Version:
 2.2.7


## Requires:
 minSdkVersion 9


## Dependencies:
 none


## Packages
 com.gency.aid

 com.gency.commons

 com.gency.commons.dialog

 com.gency.commons.file

 com.gency.commons.http

 com.gency.commons.log

 com.gency.commons.misc

 com.gency.commons.net

 com.gency.commons.time

 com.gency.util

 com.gency.version


## Usage

### com.gency.aid.GencyAID  
ユーザ管理機能が必要なアプリ向けに、ユーザ識別IDを生成するロジックを提供しています。  
「各コンテンツにてrondomUUIDをしてもらい、SharedPreferencesに保存する機構」をソースコードにしたものです。  
**あくまでもロジックのみの提供であり、uuid及び暗号キーの管理責任は各コンテンツに委ねます。  
各コンテンツで既に実装済みである場合は、GencyAIDは使用する必要はありません。(※1)**  

**com.gency.aid.GencyAIDConst**の定数はSharedPreferencesに保存する際のファイル名やキー名、暗号化に使うKEYとIVです。  
**こちらは各コンテンツで書き換えて使用してください。**  
GencyAIDConstを継承したクラスを以下のように使用してください。

**注意**：これはあくまで一例です。

```
import com.gency.aid.GencyAID;
import com.gency.aid.GencyAIDConst;

class MyGencyAIDConst extends GencyAIDConst{
    public String AID = "toiKoh8eiF";
    public String AID_CHARA = "thae3Oozae";
    public String AID_AES_KEY = "HOGEHOGEHOGEHOGEHOGEHOGEHOGEHOGE";//256bit
    public String AID_AES_IV = "PIYOPIYOPIYOPIYO";//128bit
}

・・・・
// uuidを取得したい場合
String uuid = GencyAID.getGencyAID(this,new MyGencyAIDConst);

```

### com.gency.commons.log.GencyDLog  
android.util.Logのラッパークラスです。
setDebuggable()でログ出力のOFF/ONを切り換えることができます。
```java
GencyDLog.setDebuggable(true);
GencyDLog.d(TAG, "Hello world!!!");

```

### com.gency.commons.net.GencyNetworkUtil  
ネットワークが利用可能かどうか調べることができます。
```java
if (! GencyNetworkUtil.isNetworkConnected(getApplicationContext()) {
  GencyDLog.d(TAG, "ネットワークに接続されていません。");
}

```
