plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.delivery.basic"
}

dependencies {
    api(project(":feature:delivery:order-tracking"))
    api(project(":feature:delivery:maps"))
}

