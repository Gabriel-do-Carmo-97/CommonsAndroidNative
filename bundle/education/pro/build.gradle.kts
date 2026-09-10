plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.education.pro"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:account:profile"))
    api(project(":feature:communication:message"))
    api(project(":feature:system:media-picker"))
    api(project(":feature:account:settings"))
    api(project(":feature:customer:feedback"))
    api(project(":feature:platform:ai-assistant"))
}
