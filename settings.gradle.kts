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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()

        val localProperties = java.util.Properties().apply {
            val localFile = rootDir.resolve("local.properties")
            if (localFile.exists()) {
                localFile.inputStream().use { load(it) }
            }
        }
        val gprUser = localProperties.getProperty("gpr.user")
            ?: providers.gradleProperty("gpr.user").orNull
            ?: providers.environmentVariable("GPR_USER").orNull
            ?: providers.environmentVariable("GITHUB_ACTOR").orNull
        val gprKey = localProperties.getProperty("gpr.key")
            ?: providers.gradleProperty("gpr.key").orNull
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
include(":core:testing")
project(":core:testing").projectDir = file("core/testing")

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

// Mapeamento dos Bundles como Submódulos Hierárquicos organizados por Domínio e Tier
fun includeBundle(domain: String, tier: String) {
    val bundlePath = ":bundles:$domain:$tier"
    include(bundlePath)
    project(bundlePath).projectDir = file("bundles/$domain/$tier")
}

// 📦 1. Identidade & Acesso
includeBundle("identity", "basic")
includeBundle("identity", "standard")
includeBundle("identity", "pro")

// 📦 2. Infraestrutura de App
includeBundle("foundation", "basic")
includeBundle("foundation", "standard")
includeBundle("foundation", "pro")

// 📦 3. Vendas & Varejo
includeBundle("ecommerce", "basic")
includeBundle("ecommerce", "standard")
includeBundle("ecommerce", "pro")

// 📦 4. Logística & Tempo Real
includeBundle("delivery", "basic")
includeBundle("delivery", "standard")
includeBundle("delivery", "pro")

// 📦 5. Agendamentos & Orçamentos
includeBundle("services", "basic")
includeBundle("services", "standard")
includeBundle("services", "pro")

// 📦 6. Engajamento & Suporte
includeBundle("communication", "basic")
includeBundle("communication", "standard")
includeBundle("communication", "pro")

// 📦 7. Monetização & Assinaturas
includeBundle("subscriptions", "basic")
includeBundle("subscriptions", "standard")
includeBundle("subscriptions", "pro")

// 📦 8. Comunidade & Redes Sociais
includeBundle("social", "basic")
includeBundle("social", "standard")
includeBundle("social", "pro")

// 📦 9. Escolas & Cursos
includeBundle("education", "basic")
includeBundle("education", "standard")
includeBundle("education", "pro")

// 📦 10. Saúde & Clínicas
includeBundle("health", "basic")
includeBundle("health", "standard")
includeBundle("health", "pro")

// 📦 11. Carteira Digital & Finanças
includeBundle("finance", "basic")
includeBundle("finance", "standard")
includeBundle("finance", "pro")

// 📦 12. Shows & Eventos
includeBundle("events", "basic")
includeBundle("events", "standard")
includeBundle("events", "pro")

// 📦 13. Imobiliárias & Locação
includeBundle("real-estate", "basic")
includeBundle("real-estate", "standard")
includeBundle("real-estate", "pro")

// 📦 14. Segurança & Emergência
includeBundle("emergency", "basic")
includeBundle("emergency", "standard")
includeBundle("emergency", "pro")


