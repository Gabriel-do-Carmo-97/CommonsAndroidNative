plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.finance.standard"
}

dependencies {
    api(project(":feature:checkout:payment"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:account:settings"))
}
