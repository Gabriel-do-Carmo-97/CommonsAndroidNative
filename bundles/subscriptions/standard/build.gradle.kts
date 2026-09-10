plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.subscriptions.standard"
}

dependencies {
    api(project(":feature:services:subscriptions"))
    api(project(":feature:checkout:payment"))
    api(project(":feature:storefront:promotions"))
}

