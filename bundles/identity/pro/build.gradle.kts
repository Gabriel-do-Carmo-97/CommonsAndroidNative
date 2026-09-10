plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.identity.pro"
}

dependencies {
    api(project(":feature:auth:authentication"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:account:profile"))
    api(project(":feature:account:onboarding"))
    api(project(":feature:system:multi-language"))
}

