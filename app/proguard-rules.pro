# Sentry Proguard / R8 rules
-keepattributes LineNumberTable,SourceFile
-dontwarn io.sentry.**
-keep class io.sentry.** { *; }

# Firestore Models
-keep class com.example.organshare.models.** { *; }
-keepclassmembers class com.example.organshare.models.** { *; }
