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
            artifactId = project.name
            val runNumber = System.getenv("GITHUB_RUN_NUMBER")
            version = if (!runNumber.isNullOrEmpty()) "0.0.$runNumber" else "0.0.1-SNAPSHOT"

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
