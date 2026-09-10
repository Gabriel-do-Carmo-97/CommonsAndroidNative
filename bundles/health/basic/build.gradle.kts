plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.health.basic"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:communication:message"))
}

