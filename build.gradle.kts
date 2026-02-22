import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
}

allprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint(libs.versions.ktlint.get())
            suppressLintsFor {
                step = "ktlint"
                shortCode = "standard:max-line-length"
            }
            suppressLintsFor {
                step = "ktlint"
                shortCode = "standard:no-wildcard-imports"
            }
        }

        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint(libs.versions.ktlint.get())
            suppressLintsFor {
                step = "ktlint"
                shortCode = "standard:max-line-length"
            }
            suppressLintsFor {
                step = "ktlint"
                shortCode = "standard:no-wildcard-imports"
            }
        }
    }

    configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        source.setFrom(
            "src/commonMain/kotlin",
            "src/commonTest/kotlin",
            "src/jvmMain/kotlin",
            "src/jvmTest/kotlin",
            "src/nativeMain/kotlin",
            "src/nativeTest/kotlin",
        )
    }
}

tasks {
    wrapper {
        gradleVersion = "8.13"
    }
}
