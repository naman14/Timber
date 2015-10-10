# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/naman/sdk/tools/proguard/proguard-android.txt
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

# retrofit
-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class com.naman14.timber.lastfmapi.models.** { *; }

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keepattributes Exceptions
-keepattributes Signature
-keepattributes *Annotation*

#renderscript
-keep class android.support.v8.renderscript.** { *; }

#searchview
-keep class android.support.v7.widget.SearchView { *; }
