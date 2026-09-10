plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.realestate.pro"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:storefront:search"))
    api(project(":feature:delivery:maps"))
    api(project(":feature:services:scheduling"))
    api(project(":feature:system:media-picker"))
    api(project(":feature:checkout:quotation"))
}

