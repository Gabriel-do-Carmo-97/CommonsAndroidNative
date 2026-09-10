plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.social.basic"
}

dependencies {
    api(project(":feature:account:profile"))
    api(project(":feature:communication:message"))
}
