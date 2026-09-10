plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.education.basic"
}

dependencies {
    api(project(":feature:services:scheduling"))
    api(project(":feature:account:profile"))
}
