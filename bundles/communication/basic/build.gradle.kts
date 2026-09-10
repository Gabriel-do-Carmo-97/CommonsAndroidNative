plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.communication.basic"
}

dependencies {
    api(project(":feature:communication:message"))
    api(project(":feature:communication:whatsapp-direct"))
}

