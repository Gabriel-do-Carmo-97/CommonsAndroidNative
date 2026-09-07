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
        mavenLocal()
        google()
        mavenCentral()

        val gprUser = providers.gradleProperty("gpr.user").orNull
            ?: providers.environmentVariable("GPR_USER").orNull
            ?: providers.environmentVariable("GITHUB_ACTOR").orNull
        val gprKey = providers.gradleProperty("gpr.key").orNull
            ?: providers.environmentVariable("GPR_KEY").orNull
            ?: providers.environmentVariable("GITHUB_TOKEN").orNull

        val githubRepos = listOf(
            "CommonsAndroidNative",
            "CoreAndroidNative",
            "DesignSystemAndroid",
            "OmniBackendAndroid"
        )

        githubRepos.forEach { repoName ->
            maven {
                name = "GitHubPackages-$repoName"
                url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/$repoName")
                if (!gprUser.isNullOrBlank() && !gprKey.isNullOrBlank()) {
                    credentials {
                        username = gprUser
                        password = gprKey
                    }
                }
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

// 🚀 Novos Módulos Reutilizáveis para E-commerce, Serviços e Delivery
include(":onboarding")
include(":profile")
include(":settings")
include(":biometric")
include(":media-picker")
include(":feedback")
include(":search")
include(":force-update")
include(":catalog")
include(":cart")
include(":order-tracking")
include(":promotions")
include(":scheduling")
include(":quotation")
include(":loyalty")
include(":stores")
include(":whatsapp-direct")
include(":reviews-store")

// 🚀 Módulos Avançados de Expansão (Enterprise & Logística em Tempo Real)
include(":analytics")
include(":subscriptions")
include(":driver-app")
include(":multi-language")
include(":ai-assistant")
include(":offline-sync")
include(":telemetry")
include(":geofencing")
include(":dispatch")
include(":emergency")
include(":offline-maps")
