plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.events.standard"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:checkout:payment"))
    api(project(":feature:services:scheduling"))
    api(project(":feature:delivery:maps"))
}

