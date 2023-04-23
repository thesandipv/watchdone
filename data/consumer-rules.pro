-keepclassmembers class * implements java.io.Serializable {
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
}

-keepclassmembers class com.afterroot.watchdone.data.model.** {
  *;
}

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
