# Keep Kotlin Serialization classes and serializers
-keepattributes *Annotation*, InnerClasses, Signature
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep Public API and Models
-keep class br.com.gds.maps.** { *; }

# Keep Dagger / Hilt ViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
