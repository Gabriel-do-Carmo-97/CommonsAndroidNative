plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.services.basic"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:storefront:stores"))
}
