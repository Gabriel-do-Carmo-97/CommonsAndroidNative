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

fun registerFeature(name: String, theme: String) {
    include(":$name")
    project(":$name").projectDir = file("features/$theme/$name")
}

// 🔐 Auth & Segurança
registerFeature("authentication", "auth")
registerFeature("biometric", "auth")

// 👤 Conta & Usuário
registerFeature("onboarding", "account")
registerFeature("profile", "account")
registerFeature("settings", "account")

// 🏪 Vitrine & Descoberta
registerFeature("catalog", "storefront")
registerFeature("search", "storefront")
registerFeature("promotions", "storefront")
registerFeature("stores", "storefront")

// 💳 Checkout & Compra
registerFeature("cart", "checkout")
registerFeature("payment", "checkout")
registerFeature("quotation", "checkout")

// 🚚 Entrega & Logística
registerFeature("order-tracking", "delivery")
registerFeature("maps", "delivery")
registerFeature("driver-app", "delivery")
registerFeature("dispatch", "delivery")
registerFeature("geofencing", "delivery")
registerFeature("offline-maps", "delivery")

// 💬 Comunicação
registerFeature("message", "communication")
registerFeature("whatsapp-direct", "communication")

// ⭐ Fidelidade & Satisfação
registerFeature("feedback", "customer")
registerFeature("loyalty", "customer")
registerFeature("reviews-store", "customer")

// 📅 Serviços & Assinaturas
registerFeature("scheduling", "services")
registerFeature("subscriptions", "services")

// ⚙️ Sistema & Dispositivo
registerFeature("force-update", "system")
registerFeature("media-picker", "system")
registerFeature("multi-language", "system")

// 🧠 Plataforma & Infraestrutura
registerFeature("ai-assistant", "platform")
registerFeature("analytics", "platform")
registerFeature("emergency", "platform")
registerFeature("offline-sync", "platform")
registerFeature("telemetry", "platform")
