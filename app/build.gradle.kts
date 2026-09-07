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
    // Módulos do Monorepo CommonsAndroidNative
    implementation(project(":authentication"))
    implementation(project(":maps"))
    implementation(project(":message"))
    implementation(project(":payment"))
    implementation(project(":onboarding"))
    implementation(project(":profile"))
    implementation(project(":settings"))
    implementation(project(":biometric"))
    implementation(project(":media-picker"))
    implementation(project(":feedback"))
    implementation(project(":search"))
    implementation(project(":force-update"))
    implementation(project(":catalog"))
    implementation(project(":cart"))
    implementation(project(":order-tracking"))
    implementation(project(":promotions"))
    implementation(project(":scheduling"))
    implementation(project(":quotation"))
    implementation(project(":loyalty"))
    implementation(project(":stores"))
    implementation(project(":whatsapp-direct"))
    implementation(project(":reviews-store"))

    implementation(libs.design.system)
    implementation(libs.ds.templates)
    implementation(libs.myAndroidCore)
    implementation(libs.omni.backend.core)
    implementation(libs.omni.backend.firebase)

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