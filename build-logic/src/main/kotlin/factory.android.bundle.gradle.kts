plugins {
    id("commons.android.library")
    id("commons.android.publishing")
}

dependencies {
    "testImplementation"("junit:junit:4.13.2")
    "androidTestImplementation"("androidx.test.ext:junit:1.3.0")
    "androidTestImplementation"("androidx.test.espresso:espresso-core:3.7.0")
}
