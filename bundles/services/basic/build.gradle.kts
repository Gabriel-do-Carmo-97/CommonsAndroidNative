plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.services.basic"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:storefront:stores"))
}

