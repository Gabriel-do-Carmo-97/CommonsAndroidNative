plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.emergency.basic"
}

dependencies {
    api(project(":feature:platform:emergency"))
    api(project(":feature:delivery:maps"))
}

