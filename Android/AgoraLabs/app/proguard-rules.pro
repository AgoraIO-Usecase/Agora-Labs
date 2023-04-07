# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes Signature
-keep class io.agora.**{*;}
-dontwarn javax.**
-dontwarn com.google.devtools.build.android.**

-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keep class io.agora.api.example.common.base.bean.**{*;}
-keep class io.agora.api.example.common.base.event.**{*;}
-keep class io.agora.api.example.common.model.**{*;}
-keep class io.agora.api.example.common.server.model.**{*;}
-keep class io.agora.api.example.common.server.Service{*;}
-keep class io.agora.api.example.common.server.ApiManagerService{*;}
-keep class io.agora.api.example.main.model.**{*;}
-keep class io.agora.api.example.authpack{*;}
-keep class com.example.anan.AAChartCore.AAChartCoreLib.**{*;}

-keep class com.bumptech.glide.**{*;}

-keep class com.google.gson.** {*;}
-keep class org.json.** {*;}
-keep class sun.misc.Unsafe { *; }

-keep class com.squareup.**{*;}

-keep class com.faceunity.wrapper.**{*;}
-keep class com.bef.effectsdk.**{*;}
-keep class com.amazing.**{*;}
-keep class com.bytedance.**{*;}


