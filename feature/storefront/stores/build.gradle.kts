plugins {
    id("commons.android.library")
    id("commons.android.compose")
    id("commons.android.publishing")
    id("commons.android.hilt")
}

android {
    namespace = "br.com.wgc.stores"
}

dependencies {
    implementation(libs.design.system)
    implementation(libs.myAndroidCore)
    implementation(libs.core.location)
    implementation(libs.omni.backend.core)
    implementation(libs.omni.backend.firebase)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
