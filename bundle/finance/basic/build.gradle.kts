plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.finance.basic"
}

dependencies {
    api(project(":feature:checkout:payment"))
}
