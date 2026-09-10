plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.health.standard"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:communication:message"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:system:media-picker"))
}
