
# Gency/sub_module_for_cybird/cy_gencydid
Cybird 製アプリ向け。  
端末識別情報をもとにUUIDを取得する機能を提供する。

## Version:
 2.2.8


## Requires:
 minSdkVersion 9　(14を推奨)


## Dependencies:
 none


## Packages
 com.gency.did

 com.gency.version

## Usage

***このモジュールの推奨APILevelは14以上です。  
※API Levelが13以下であるとユニークIDとして担保できないため***

端末固有のIDを一部利用した独自uuidを取得できます。
```java
    String uuid = com.gency.did.GencyDID.get(this);
```
想定している使用方法

* ユーザの行動をトラッキング
* クラッシュや不具合時の解析

**注意**  

* サービスのユーザデータを管理するためのユーザIDとして使用することを非推奨とします。  
理由はGoogleの仕様改変などにより、このUUIDの生成元ロジックが正常に動作しなくなる可能性があるからです。
* soファイルの種類が多いので、状況に合わせて使用するsoファイルを選定してください。  
例) jniLibs/armeabiのみにする　など。
* dependenciesではなく、cy_gencydidをプロジェクトにそのまま追加する場合は、build.gradleを以下のように書きなおしてください。
```java
// cy_gencydid/build.gradle
apply plugin: 'com.android.library'
android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"
    defaultConfig {
        minSdkVersion 14
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
}
```

***
