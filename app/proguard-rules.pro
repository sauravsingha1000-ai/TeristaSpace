# ProGuard rules
-keep class androidx.compose.** { *; }
-keep class com.teristaspace.** { *; }
-keep class * extends androidx.lifecycle.ViewModel {
    public <init>();
}

