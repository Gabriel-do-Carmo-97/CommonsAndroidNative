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

fun registerFeature(theme: String, name: String) {
    include(":feature:$theme:$name")
    project(":feature:$theme:$name").projectDir = file("feature/$theme/$name")
}

// 🔐 Auth & Segurança
registerFeature("auth", "authentication")
registerFeature("auth", "biometric")

// 👤 Conta & Usuário
registerFeature("account", "onboarding")
registerFeature("account", "profile")
registerFeature("account", "settings")

// 🏪 Vitrine & Descoberta
registerFeature("storefront", "catalog")
registerFeature("storefront", "search")
registerFeature("storefront", "promotions")
registerFeature("storefront", "stores")

// 💳 Checkout & Compra
registerFeature("checkout", "cart")
registerFeature("checkout", "payment")
registerFeature("checkout", "quotation")

// 🚚 Entrega & Logística
registerFeature("delivery", "order-tracking")
registerFeature("delivery", "maps")
registerFeature("delivery", "driver-app")
registerFeature("delivery", "dispatch")
registerFeature("delivery", "geofencing")
registerFeature("delivery", "offline-maps")

// 💬 Comunicação
registerFeature("communication", "message")
registerFeature("communication", "whatsapp-direct")

// ⭐ Fidelidade & Satisfação
registerFeature("customer", "feedback")
registerFeature("customer", "loyalty")
registerFeature("customer", "reviews-store")

// 📅 Serviços & Assinaturas
registerFeature("services", "scheduling")
registerFeature("services", "subscriptions")

// ⚙️ Sistema & Dispositivo
registerFeature("system", "force-update")
registerFeature("system", "media-picker")
registerFeature("system", "multi-language")

// 🧠 Plataforma & Infraestrutura
registerFeature("platform", "ai-assistant")
registerFeature("platform", "analytics")
registerFeature("platform", "emergency")
registerFeature("platform", "offline-sync")
registerFeature("platform", "telemetry")

// 📦 Commercial Bundles (14 domains x 3 tiers = 42 bundles)
fun registerBundle(domain: String, tier: String) {
    val path = ":bundle:$domain:$tier"
    include(path)
    project(path).projectDir = file("bundle/$domain/$tier")
}

val bundleDomains = listOf(
    "communication",
    "delivery",
    "ecommerce",
    "education",
    "emergency",
    "events",
    "finance",
    "foundation",
    "health",
    "identity",
    "real-estate",
    "services",
    "social",
    "subscriptions"
)
val bundleTiers = listOf("basic", "standard", "pro")

bundleDomains.forEach { domain ->
    bundleTiers.forEach { tier ->
        registerBundle(domain, tier)
    }
}

