plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.emergency.pro"
}

dependencies {
    api(project(":feature:platform:emergency"))
    api(project(":feature:delivery:maps"))
    api(project(":feature:platform:telemetry"))
    api(project(":feature:communication:whatsapp-direct"))
    api(project(":feature:delivery:geofencing"))
    api(project(":feature:delivery:offline-maps"))
}
