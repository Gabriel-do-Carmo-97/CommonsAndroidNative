plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.education.basic"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:account:profile"))
}

