plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.social.standard"
}

dependencies {
    api(project(":feature:account:profile"))
    api(project(":feature:communication:message"))
    api(project(":feature:system:media-picker"))
    api(project(":feature:customer:reviews-store"))
}
