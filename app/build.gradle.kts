plugins {
    id("commons.android.application")
    id("commons.android.compose")
    id("commons.android.hilt")
}

android {
    namespace = "br.com.wgc.commonsandroidnative"

    defaultConfig {
        applicationId = "br.com.wgc.commonsandroidnative"
        versionCode = 1
        versionName = "1.0"
    }
}

hilt {
    enableAggregatingTask = false
}

dependencies {
    // Módulos do Monorepo CommonsAndroidNative
    implementation(project(":feature:auth:authentication"))
    implementation(project(":feature:auth:biometric"))
    implementation(project(":feature:account:onboarding"))
    implementation(project(":feature:account:profile"))
    implementation(project(":feature:account:settings"))
    implementation(project(":feature:storefront:catalog"))
    implementation(project(":feature:storefront:search"))
    implementation(project(":feature:storefront:promotions"))
    implementation(project(":feature:storefront:stores"))
    implementation(project(":feature:checkout:cart"))
    implementation(project(":feature:checkout:payment"))
    implementation(project(":feature:checkout:quotation"))
    implementation(project(":feature:delivery:order-tracking"))
    implementation(project(":feature:delivery:maps"))
    implementation(project(":feature:communication:message"))
    implementation(project(":feature:communication:whatsapp-direct"))
    implementation(project(":feature:customer:feedback"))
    implementation(project(":feature:customer:loyalty"))
    implementation(project(":feature:customer:reviews-store"))
    implementation(project(":feature:services:scheduling"))
    implementation(project(":feature:system:force-update"))
    implementation(project(":feature:system:media-picker"))

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
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}