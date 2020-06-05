
# Gency/sub_module/cryptlib
 AES/RSA暗号化・復号化を提供する。

## Version:
 2.2.8


## Requires:
 minSdkVersion 9


## Dependencies:
 none


## Packages

 com.gency.crypto

 com.gency.version



## Usage

### com.gency.crypto.aes.GencyAES
AESによる暗号化・復号化を行います。
```java
byte[] data;
// 暗号化
byte[] cryptData = GencyAES.encrypt(data, /*AES_KEY*/, /*AID_AES_IV*/);
// 復号化
byte[] encryptData = GencyAES.decrypt(cryptData, /*AES_KEY*/, /*AES_IV*/);
```

### com.gency.crypto.rsa.GencyRSA
RSAによる暗号化・復号化を行います。
```java
byte[] data;
// 暗号化
byte[] cryptData = GencyRSA.encryptData(data, /*PublicKey*/);
// 復号化
byte[] encryptData = GencyRSA.decryptData(cryptData, /*PrivateKey*/);
```
