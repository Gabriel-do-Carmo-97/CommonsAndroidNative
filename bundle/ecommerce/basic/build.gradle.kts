plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.ecommerce.basic"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:checkout:cart"))
    api(project(":feature:checkout:payment"))
}
