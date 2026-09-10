plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.communication.pro"
}

dependencies {
    api(project(":feature:communication:message"))
    api(project(":feature:communication:whatsapp-direct"))
    api(project(":feature:system:media-picker"))
    api(project(":feature:customer:feedback"))
    api(project(":feature:platform:ai-assistant"))
}

