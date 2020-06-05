# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/n01008/Developer/adt-bundle-mac-x86_64-20140624/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Deprecated

# com.gency.crypto
-keep class com.gency.crypto.aes.GencyAES { public protected *; }
-keep class com.gency.crypto.aes.GencyAESUtility { public protected *; }
-keep class com.gency.crypto.rsa.GencyRSA { public protected *; }
-keep class com.gency.version.CryptoLibVersion { public protected *; }
-keep class com.gency.crypto.rsa.GencyDLog {
    public static void setDebuggable(*);
}
