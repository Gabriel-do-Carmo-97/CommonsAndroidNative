plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.services.standard"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:storefront:stores"))
    api(project(":feature:checkout:quotation"))
    api(project(":feature:customer:feedback"))
}

