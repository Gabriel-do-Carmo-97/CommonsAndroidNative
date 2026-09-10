plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.subscriptions.basic"
}

dependencies {
    api(project(":feature:services:subscriptions"))
    api(project(":feature:checkout:payment"))
}
