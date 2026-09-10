plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.communication.basic"
}

dependencies {
    api(project(":feature:communication:message"))
    api(project(":feature:communication:whatsapp-direct"))
}
