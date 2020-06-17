-keepclassmembers class * implements java.io.Serializable {
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
}

-keepclassmembers class com.afterroot.tmdbapi.model.** {
  *;
}

-keepclassmembers class com.afterroot.tmdbapi2.model.** {
  *;
}