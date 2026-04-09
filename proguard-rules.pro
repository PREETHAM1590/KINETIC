# KINETIC ProGuard / R8 Rules
-keepattributes *Annotation*
-keep class com.kinetic.app.data.models.** { *; }

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }

# Firebase
-keepattributes Signature
-keep class com.google.firebase.** { *; }

# DataStore
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
  <fields>;
}
