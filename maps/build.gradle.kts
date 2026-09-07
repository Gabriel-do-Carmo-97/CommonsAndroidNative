plugins {
    id("commons.android.library")
    id("commons.android.compose")
    id("commons.android.publishing")
    id("commons.android.hilt")
}

android {
    namespace = "br.com.gds.maps"
}

dependencies {
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}