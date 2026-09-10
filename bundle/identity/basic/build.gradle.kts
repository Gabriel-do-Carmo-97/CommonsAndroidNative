plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.identity.basic"
}

dependencies {
    api(project(":feature:auth:authentication"))
    api(project(":feature:account:profile"))
}
