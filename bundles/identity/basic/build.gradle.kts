plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.identity.basic"
}

dependencies {
    api(project(":feature:auth:authentication"))
    api(project(":feature:account:profile"))
}

