plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.delivery.pro"
}

dependencies {
    api(project(":feature:delivery:order-tracking"))
    api(project(":feature:delivery:maps"))
    api(project(":feature:delivery:driver-app"))
    api(project(":feature:platform:telemetry"))
    api(project(":feature:delivery:dispatch"))
    api(project(":feature:delivery:geofencing"))
    api(project(":feature:delivery:offline-maps"))
}

