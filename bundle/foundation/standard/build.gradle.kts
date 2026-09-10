plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.foundation.standard"
}

dependencies {
    api(project(":feature:account:settings"))
    api(project(":feature:system:force-update"))
    api(project(":feature:platform:analytics"))
}
