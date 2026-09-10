plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.identity.standard"
}

dependencies {
    api(project(":feature:auth:authentication"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:account:profile"))
}
