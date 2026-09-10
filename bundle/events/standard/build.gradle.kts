plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.events.standard"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:checkout:payment"))
    api(project(":feature:services:scheduling"))
    api(project(":feature:delivery:maps"))
}
