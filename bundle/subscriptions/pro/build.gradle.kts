plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.subscriptions.pro"
}

dependencies {
    api(project(":feature:services:subscriptions"))
    api(project(":feature:checkout:payment"))
    api(project(":feature:storefront:promotions"))
    api(project(":feature:system:force-update"))
    api(project(":feature:platform:analytics"))
}
