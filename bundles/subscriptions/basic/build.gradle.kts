plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.subscriptions.basic"
}

dependencies {
    api(project(":feature:services:subscriptions"))
    api(project(":feature:checkout:payment"))
}

