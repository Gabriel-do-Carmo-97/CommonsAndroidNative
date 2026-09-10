plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.realestate.standard"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:storefront:search"))
    api(project(":feature:delivery:maps"))
    api(project(":feature:services:scheduling"))
}
