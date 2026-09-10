plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.events.basic"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:checkout:payment"))
}

