plugins {
    id("com.bazaar.build")
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries {
            executable {
                entryPoint = "com.bazaar.cli.main"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.bazaarParser)
                implementation(libs.kotlinx.io.core)
            }
        }
    }
}
