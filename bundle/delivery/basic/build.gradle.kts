plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.delivery.basic"
}

dependencies {
    api(project(":feature:delivery:order-tracking"))
    api(project(":feature:delivery:maps"))
}
