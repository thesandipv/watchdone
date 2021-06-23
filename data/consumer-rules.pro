-keepclassmembers class * implements java.io.Serializable {
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
}

-keepclassmembers class com.afterroot.watchdone.data.model.** {
  *;
}
