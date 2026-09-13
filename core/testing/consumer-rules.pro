# Consumer Proguard rules for :core:testing
-keepclassmembers class * {
    @org.junit.Rule *;
    @org.junit.Test *;
}
