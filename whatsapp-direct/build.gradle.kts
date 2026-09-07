plugins {
    id("commons.android.library")
    id("commons.android.compose")
    id("commons.android.publishing")
    id("commons.android.hilt")
}

android {
    namespace = "br.com.gds.whatsapp_direct"
}

dependencies {
    implementation(libs.design.system)
    implementation(libs.myAndroidCore)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}
