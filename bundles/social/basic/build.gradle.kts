plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.social.basic"
}

dependencies {
    api(project(":feature:account:profile"))
    api(project(":feature:communication:message"))
}

