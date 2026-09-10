plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.emergency.basic"
}

dependencies {
    api(project(":feature:platform:emergency"))
    api(project(":feature:delivery:maps"))
}
