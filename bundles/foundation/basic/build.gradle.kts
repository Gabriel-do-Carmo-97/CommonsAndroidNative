plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.foundation.basic"
}

dependencies {
    api(project(":feature:account:settings"))
    api(project(":feature:system:force-update"))
}

