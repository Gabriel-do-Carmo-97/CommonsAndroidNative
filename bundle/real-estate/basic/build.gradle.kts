plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.realestate.basic"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:storefront:search"))
}
