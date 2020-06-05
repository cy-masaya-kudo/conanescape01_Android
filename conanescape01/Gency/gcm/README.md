
# Gency/sub_module/gcm
 Google Cloud Messaging(GCM), Firbase Cloud Messaging(FCM) を利用してNotificationを送信する機能を提供する。

## Version:
 2.2.7


## Requires:
 minSdkVersion 14


## Dependencies:
  com.google.android.gms:play-services-gcm:15.0.1

  com.gency:gency:*

  com.gency:cryptolib:*



## Packages
 com.gency.gcm

 com.gency.version


## Usage

### GCM関連実装手順  

1. AndroidManifest.xml

  GencyRegistrationIntentService内で自社サーバーへのレジスターメソッドを実装してください。  
  なお、以下はgcm使用時は全て必須の設定事項です。

  - 必要なパーミッション一覧
  ```xml
      <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
      <uses-permission android:name="android.permission.WAKE_LOCK" />
      <uses-permission android:name="android.permission.VIBRATE" />
  ```
   - API Level 14以下をサポートする場合  
  ```xml
      <uses-permission android:name="android.permission.GET_ACCOUNTS" />
  ```
  - API Level 9をサポートする場合  
   ```xml
    <permission
        android:name="<YOUR.PACKAGE.NAME>.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />
    <uses-permission
        android:name="<YOUR.PACKAGE.NAME>.permission.C2D_MESSAGE" />
   ```

- 起動したいActivityの指定  
   ※PUSHを受け取って起動したいActivityのintent-filterタグの中に、以下の行を追加してください。
   ```xml
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:scheme="app" android:host="YOUR_APPLICATION_PACKAGE_NAME" />
        </intent-filter>
    </activity>
  ```

  - サービスおよびレシーバーの実装  
  ```xml
    <!-- [START gcm_receiver] -->
    <activity
        android:name="com.gency.gcm.GencyPrefsActivity"
        android:launchMode="singleTask"
        />
    <activity
        android:name="com.gency.gcm.GencyGCMIntermediateActivity"
        android:launchMode="singleInstance"
        />
    <activity
        android:name="com.gency.gcm.GencyCustomDialogActivity"
        android:launchMode="singleInstance"
        />
    <receiver
        android:name="com.google.android.gms.gcm.GcmReceiver"
        android:exported="true"
        android:permission="com.google.android.c2dm.permission.SEND" >
        <intent-filter>
            <action android:name="com.google.android.c2dm.intent.RECEIVE" />
            <category android:name=<YOUR.PACKAGE.NAME> />
        </intent-filter>
    </receiver>
    <!-- [END gcm_receiver] -->

    <!-- [START gcm_service] -->
    <service
        android:name="com.gency.gcm.GencyRegistrationIntentService"
        android:exported="false">
    </service>
    <!-- [END gcm_service] -->

    <!-- [START gcm_listener] -->
    <service
        android:name="com.gency.gcm.GencyGcmListenerService"
        android:exported="false" >
        <intent-filter>
            <action android:name="com.google.android.c2dm.intent.RECEIVE" />
        </intent-filter>
    </service>
    <!-- [END gcm_listener] -->

    <!-- [START instanceId_listener] -->
    <service
        android:name="com.gency.gcm.GencyInstanceIDListenerService"
        android:exported="false">
        <intent-filter>
            <action android:name="com.google.android.gms.iid.InstanceID"/>
        </intent-filter>
    </service>
    <!-- [END instanceId_listener] -->
    ```

- アプリ側strings.xmlの実装  
  ```xml
      <!-- GCM SENDER ID -->
      <string name="LIB_GCM_SENDERID">000000000000</string>
  ```
  各アプリのsender idを設定すること。  

- runGCMをする箇所について  

  - モジュールを外部のPushサービスとつなぎこむ場合  
    GencyGCMUtilitiesE#runGCM()をすると以下のレアなケースにて処理が進まない可能性がある。  
    以下をrunGCM()を実装しているActivityにて実装すること。
  ```java
      protected void onCreate(Bundle savedInstanceState) {
          startGCM();
      }

      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          startGCM();
      }

      private void startGCM(){
          try {
              GencyGCMUtilitiesE.runGCM(this, new MyGencyGCMTokenRegister());
          } catch (Exception e) {
              e.printStackTrace();
          }
      }

      class MyGencyGCMTokenRegister extends GencyGCMTokenRegister{
          @Override
          public void sendRegistrationInfo(Context context, String token, String userAgent) throws NoSuchPaddingException,
                          InvalidAlgorithmParameterException,
                          NoSuchAlgorithmException,
                          IllegalBlockSizeException,
                          BadPaddingException,
                          InvalidKeyException,
                          UnsupportedEncodingException {
              // registration idが取得できるので使用したいPushサービスへ送る
          }

          @Override
          public void sendUnregistrationInfo(Context context, String token, String userAgent) throws NoSuchPaddingException,
                          InvalidAlgorithmParameterException,
                          NoSuchAlgorithmException,
                          IllegalBlockSizeException,
                          BadPaddingException,
                          InvalidKeyException,
                          UnsupportedEncodingException {
              // GencyGCMUtilitiesE#unregisterGCMを呼び出すとこのメソッドにコールバックされる
          }
      }    
  ```

  - CybirdのPUSHサービスを使用する場合  
  社内のPUSHを使用したい場合はsub_module/gcm_sub_toPUSH/に実装されている  
  com.gency.gcm.GencyGCMTokenRegisterManagerをrunGCMの第2引数に渡してください。
  ```java
      private void startGCM(){
          try {
              // 最後のbooleanはtrue:POPgate, false:AES をPUSH都の通信時に使用。
              GencyGCMUtilitiesE.runGCM(this,
                   new GencyGCMTokenRegisterManager(
                       getUUID(), <任意のA-UUID>, getDUUID(),
                       false));
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  ```

  - CybirdのPUSHサービスを使用し、開発もサイバード社内の場合  
  sub_module_for_cybird/cy_gcm/に実装されている「GencyGCMUtilities」を使用する。  
  なお、GencyGCMUtilitiesによって以下の２つUUIDが自動的にPUSHサービスへ送られることになる。  

       com.gency.cybirdid.CybirdCommonUserId.get()  
       com.gency.gencydid.GencyDID.get()  
  ```java
      private void startGCM(){
          try {
              // 最後のbooleanはtrue:POPgate, false:AES をPUSH都の通信時に使用。
              GencyGCMUtilities.runGCM(this);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  ```
  アプリ内でユーザ管理用にUUIDを持っている場合は、第２引数にセットする。
  ```java
      private void startGCM(){
          try {
              // GencyAID(※1):GencyAIDを使用している場合は第２引数にセットすること。
              // 使用していない場合は各コンテンツで発行しているUUIDを設定すること。
              // IETF RFC 4122 に則った文字列の並びであること。
              GencyGCMUtilities.runGCM(this, <任意のA-UUID>);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  ```

***

### ステータスバーに表示するアイコンを変更する  
プッシュ通知を受信したときに通知バーに表示するアイコンを変更します。  
プッシュ通知はアプリがバックグラウンドにいない時も受信するため、アプリのApplication#onCreate()の中でセットしてください。  
- setSmallIcon()で、通知バーに表示するアイコン(小)をできます。  
Android5.0以上の端末ではアイコンの透明色以外の色が塗りつぶされて表示されてしまうため、白色と透明色だけの画像を用意してください。  
- setLargeIcon()で、Android4.0以上において通知バーを開いたときに表示されるアイコン(大)を変更できます。
ただしAndroid4.0未満では設定しても変化はありません。  
- setIconBgColor()で、通知バーを開いたときに表示されるアイコン(小)の背景色を変更できます。
```java
public class MainApplication extends Application {
@Override
    public void onCreate() {
        super.onCreate();
        // 通知バーに表示するアイコン(小) 白色と透明色だけのアイコンを指定すること
        GencyGCMUtilities.setSmallIcon(this, R.drawable.ic_notification_android);
        // 通知バーを開いたときに表示されるアイコン(大) を変更する
        GencyGCMUtilities.setLargeIcon(this, R.mipmap.ic_launcher);
        // 通知バーを開いたときに表示されるアイコン(小)の背景色
        GencyGCMUtilities.setIconBgColor(this, 0xA0FF0000);
        
        // 通知のデフォルトチャネルの表示名を指定する
        GencyGCMUtilitiesE.setDefaultChannel(this, "Default Name");
        // 通知のデフォルトチャネルを指定する（ID:"gcm_channel_id" name:"デフォルトチャンネル"）
        // GencyGCMUtilitiesE.setDefaultChannel(this);
    }
}
```


***

### プッシュ通知確認ダイアログの表記について  
デフォルトでは、以下の文言が表示されます。  
以下のリソースIDに設定することで、文言を変更できます。
* 見出し

        デフォルト文言　：　Agreement  
        リソースID　　　：　LIB_GCM_DIALOG_TITLE

* [同意する]ボタン

        デフォルト文言　：　Agree  
        リソースID　　　：　LIB_GCM_DIALOG_AGREE

* [同意しない]ボタン

        デフォルト文言　：　Decline  
        リソースID　　　：　LIB_GCM_DIALOG_DECLINE

***
