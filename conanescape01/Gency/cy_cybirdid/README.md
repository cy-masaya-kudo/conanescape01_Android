
# Gency/sub_module_for_cybird/cy_cybirdid
 Cybird 製アプリ向け。
 UUIDを取得する機能を提供する。

## Version:
 2.2.8


## Requires:
 minSdkVersion 9


## Dependencies:
 jp.co.cybird.app.android.lib:CybirdUtility:1.0.2

 com.gency:cryptolib:*



## Packages
 com.gency.cybirdid


## Usage

### CybirdCommonUserId
旧バージョンCybirdUtility_1.0.1を取り込む必要はなく、cy_cybirdidを利用するだけで
jp.co.cybird.app.android.lib.cybirdid.CybirdCommonUserIdから取得できる
UUIDをサルベージしつつ、 利用することが可能です。
```java
String uuid = com.gency.cybirdid.CybirdCommonUserId.get(context);
```

- **注意**  
新規アプリに搭載する場合、  
このCybirdCommonUserIdを、ユーザ管理するためのユーザーIDとして使用しないこと。
