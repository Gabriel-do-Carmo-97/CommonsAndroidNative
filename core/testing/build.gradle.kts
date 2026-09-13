plugins {
    id("commons.android.library")
    id("commons.android.publishing")
}

android {
    namespace = "br.com.wgc.testing"
}

dependencies {
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    api(libs.mockk)
    api(libs.omni.backend.core)
    api(libs.androidx.core.ktx)
}
