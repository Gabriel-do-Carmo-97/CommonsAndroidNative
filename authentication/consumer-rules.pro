# Keep Kotlin Serialization classes and serializers
-keepattributes *Annotation*, InnerClasses, Signature
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * extends kotlinx.serialization.KSerializer {
    <fields>;
    <init>(...);
}
-keepclassmembers class **$Companion {
    public <methods>;
}

# Keep Public API and Models
-keep class br.com.gds.authentication.model.** { *; }
-keep class br.com.gds.authentication.config.** { *; }
-keep class br.com.gds.authentication.session.** { *; }
-keep class br.com.gds.authentication.navigation.** { *; }

# Keep Dagger / Hilt ViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
