plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.subscriptions.standard"
}

dependencies {
    api(project(":feature:services:subscriptions"))
    api(project(":feature:checkout:payment"))
    api(project(":feature:storefront:promotions"))
}
