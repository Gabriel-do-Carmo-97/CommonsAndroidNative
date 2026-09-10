plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.health.pro"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:communication:message"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:system:media-picker"))
    api(project(":feature:checkout:quotation"))
    api(project(":feature:platform:emergency"))
}

