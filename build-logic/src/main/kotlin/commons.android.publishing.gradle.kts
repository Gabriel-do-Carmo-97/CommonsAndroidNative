import com.android.build.api.dsl.LibraryExtension

plugins {
    id("maven-publish")
}

pluginManager.withPlugin("com.android.library") {
    extensions.configure<LibraryExtension> {
        publishing {
            singleVariant("release") {
                withSourcesJar()
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "br.com.wgc"
            val domain = project.parent?.name
            val tier = project.name
            artifactId = if (project.parent?.parent?.name == "bundle" || project.parent?.parent?.name == "bundles") {
                "bundle-$domain-$tier"
            } else {
                project.name
            }
            val versionName = (findProperty("VERSION_NAME") as? String)
                ?: System.getenv("VERSION_NAME")
                ?: System.getenv("GITHUB_RUN_NUMBER")?.let { "0.0.$it" }
                ?: "0.0.1-SNAPSHOT"
            version = versionName

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CommonsAndroidNative")
            credentials {
                username = System.getenv("GPR_USER")
                    ?: findProperty("gpr.user") as? String
                    ?: System.getenv("GITHUB_ACTOR")
                password = System.getenv("GPR_KEY")
                    ?: findProperty("gpr.key") as? String
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
