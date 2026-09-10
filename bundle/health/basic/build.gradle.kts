plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.health.basic"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:communication:message"))
}
