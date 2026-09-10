plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.emergency.standard"
}

dependencies {
    api(project(":feature:platform:emergency"))
    api(project(":feature:delivery:maps"))
    api(project(":feature:platform:telemetry"))
    api(project(":feature:communication:whatsapp-direct"))
}
