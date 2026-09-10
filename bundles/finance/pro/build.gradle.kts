plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.finance.pro"
}

dependencies {
    api(project(":feature:checkout:payment"))
    api(project(":feature:auth:biometric"))
    api(project(":feature:account:settings"))
    api(project(":feature:services:subscriptions"))
    api(project(":feature:platform:analytics"))
    api(project(":feature:platform:ai-assistant"))
}

