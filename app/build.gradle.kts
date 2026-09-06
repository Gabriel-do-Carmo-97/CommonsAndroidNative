plugins {
    id("commons.android.application")
    id("commons.android.compose")
    id("commons.android.hilt")
}

android {
    namespace = "br.com.gds.commonsandroidnative"

    defaultConfig {
        applicationId = "br.com.gds.commonsandroidnative"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":authentication"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}