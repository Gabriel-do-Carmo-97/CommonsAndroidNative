// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka) apply false
}

import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

apply(plugin = "jacoco")

configure<JacocoPluginExtension> {
    toolVersion = "0.8.12"
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.12"
    }

    afterEvaluate {
        extensions.findByName("detekt")?.let {
            val detektExt = it as? io.gitlab.arturbosch.detekt.extensions.DetektExtension
            detektExt?.buildUponDefaultConfig = true
            detektExt?.config?.setFrom(files("${rootProject.rootDir}/config/detekt/detekt.yml"))
            detektExt?.ignoreFailures = true
        }

        extensions.findByName("android")?.let { androidExt ->
            val ext = androidExt as? com.android.build.gradle.BaseExtension
            ext?.buildTypes?.findByName("debug")?.let { debugType ->
                debugType.isTestCoverageEnabled = true
            }
        }
    }
}

val fileFilter = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*_MembersInjector.class",
    "**/Dagger*Component.class",
    "**/Dagger*Component$*.class",
    "**/*_Factory.class",
    "**/*_Factory$*.class",
    "**/*_HiltModules*.class",
    "**/*Hilt_*.class",
    "**/*ComposableSingletons*.*",
    "**/*_ComponentTreeDeps*.*"
)

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "Reporting"
    description = "Gera relatorio unificado de cobertura JaCoCo (HTML e XML) para todo o monorepo."

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacoco.xml"))
    }

    val execFiles = fileTree(rootDir) {
        include("**/build/outputs/unit_test_code_coverage/debugUnitTest/*.exec")
        include("**/build/jacoco/*.exec")
    }
    executionData.setFrom(execFiles)

    val subprojectsToCover = subprojects.filter { subproj ->
        subproj.childProjects.isEmpty() && (
            subproj.path.startsWith(":feature:") ||
            subproj.path.startsWith(":core:") ||
            subproj.path == ":app"
        )
    }

    dependsOn(subprojectsToCover.map { "${it.path}:testDebugUnitTest" })

    val sources = files(subprojectsToCover.map { "${it.projectDir}/src/main/java" })
    sourceDirectories.setFrom(sources)

    val classes = subprojectsToCover.map { subproj ->
        fileTree("${subproj.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
            exclude(fileFilter)
        }
    }
    classDirectories.setFrom(files(classes))
}