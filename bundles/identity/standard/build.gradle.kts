plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.identity.standard"
}

dependencies {
    api(project(":feature:auth:authentication"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:account:profile"))
}

