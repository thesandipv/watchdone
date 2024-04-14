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

-verbose
-allowaccessmodification
-repackageclasses

-keepattributes SourceFile,
                LineNumberTable,
                Signature,
                RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                AnnotationDefault

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-keep class com.afterroot.watchdone.data.model.** {
  *;
}

-keep class com.afterroot.data.model.** {
  *;
}

-keep class * extends com.afterroot.watchdone.base.compose.* {
  *;
}


# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# We only need to keep ComposeView
-keep public class androidx.compose.ui.platform.ComposeView {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
#-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Using ktor client in Android has missing proguard rule
# See https://youtrack.jetbrains.com/issue/KTOR-5528
-dontwarn org.slf4j.**

-adaptresourcefilenames okhttp3/internal/publicsuffix/jd7.gz
