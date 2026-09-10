plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.events.basic"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:checkout:payment"))
}
