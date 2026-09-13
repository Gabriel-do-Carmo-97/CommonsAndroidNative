# ==============================================================================
# Enterprise R8 & ProGuard Optimization Rules - CommonsAndroidNative
# ==============================================================================

# --- General Optimizations ---
-allowaccessmodification
-repackageclasses ''

# Preserve line numbers and source files for crash reports and stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve annotations and signatures for Kotlin and reflection
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# --- Jetpack Compose ---
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keep class * extends androidx.compose.ui.node.ModifierNodeElement { *; }

# --- Kotlin Coroutines & Flow ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# --- Dagger / Hilt ---
-dontwarn dagger.hilt.**
-dontwarn javax.inject.**
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.android.internal.builders.** { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# --- Kotlin Reflection & Metadata ---
-keepattributes RuntimeVisible*Annotations*
-keepclassmembers class kotlin.Metadata {
    public <fields>;
    public <methods>;
}

# --- OmniBackend & Serialization ---
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepclassmembers enum * { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @kotlinx.serialization.SerialName <fields>;
}
-dontwarn br.wgc.omnibackend.**
-keep class br.wgc.omnibackend.** { *; }

# --- WGC Design System & Commons ---
-dontwarn br.com.wgc.**
-keep class br.com.wgc.commonsandroidnative.** { *; }
-keep class br.com.wgc.telemetry.boundary.** { *; }

# --- AndroidX ProfileInstaller ---
-keep class androidx.profileinstaller.** { *; }
-dontwarn androidx.profileinstaller.**

# --- Optional transitive libraries (e.g. Firebase VertexAI / Ktor) ---
-dontwarn io.ktor.**
-dontwarn com.google.firebase.vertexai.**