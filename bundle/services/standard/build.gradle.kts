plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.services.standard"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:storefront:stores"))
    api(project(":feature:checkout:quotation"))
    api(project(":feature:customer:feedback"))
}
