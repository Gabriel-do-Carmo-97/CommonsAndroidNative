plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.education.standard"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:account:profile"))
    api(project(":feature:communication:message"))
    api(project(":feature:system:media-picker"))
}
