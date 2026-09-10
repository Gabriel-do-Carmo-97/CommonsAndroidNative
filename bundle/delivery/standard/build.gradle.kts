plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.delivery.standard"
}

dependencies {
    api(project(":feature:delivery:order-tracking"))
    api(project(":feature:delivery:maps"))
    api(project(":feature:delivery:driver-app"))
    api(project(":feature:platform:telemetry"))
}
