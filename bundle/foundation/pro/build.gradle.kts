plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.foundation.pro"
}

dependencies {
    api(project(":feature:account:settings"))
    api(project(":feature:system:force-update"))
    api(project(":feature:platform:analytics"))
    api(project(":feature:system:multi-language"))
    api(project(":feature:platform:offline-sync"))
}
