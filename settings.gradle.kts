pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CommonsAndroidNative")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: providers.environmentVariable("GPR_USER").orNull
                password = providers.gradleProperty("gpr.key").orNull ?: providers.environmentVariable("GPR_KEY").orNull
            }
        }
        maven {
            name = "GitHubPackagesCore"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: providers.environmentVariable("GPR_USER").orNull
                password = providers.gradleProperty("gpr.key").orNull ?: providers.environmentVariable("GPR_KEY").orNull
            }
        }
        maven {
            name = "GitHubPackagesDS"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/DesignSystemAndroid")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: providers.environmentVariable("GPR_USER").orNull
                password = providers.gradleProperty("gpr.key").orNull ?: providers.environmentVariable("GPR_KEY").orNull
            }
        }
        maven {
            name = "GitHubPackagesOmniBackend"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/OmniBackendAndroid")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: providers.environmentVariable("GPR_USER").orNull
                password = providers.gradleProperty("gpr.key").orNull ?: providers.environmentVariable("GPR_KEY").orNull
            }
        }
    }
}

rootProject.name = "CommonsAndroidNative"
include(":app")
include(":maps")
include(":message")
include(":payment")
include(":authentication")
