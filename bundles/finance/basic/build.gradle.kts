plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.finance.basic"
}

dependencies {
    api(project(":feature:checkout:payment"))
}

