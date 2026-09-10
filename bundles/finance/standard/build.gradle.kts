plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.finance.standard"
}

dependencies {
    api(project(":feature:checkout:payment"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:account:settings"))
}

