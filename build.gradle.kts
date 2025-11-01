//import com.diffplug.gradle.spotless.SpotlessExtension

//plugins {
//    alias(libs.plugins.detekt)
//    alias(libs.plugins.kotlin.multiplatform) apply false
//    alias(libs.plugins.spotless)
//}

//allprojects {
//    apply(plugin = "com.diffplug.spotless")
//    apply(plugin = "io.gitlab.arturbosch.detekt")
//
//    configure<SpotlessExtension> {
//        kotlin {
//            target("**/*.kt")
//            ktlint(libs.versions.ktlint.get())
//        }
//
//        kotlinGradle {
//            target("**/*.gradle.kts")
//            ktlint(libs.versions.ktlint.get())
//        }
//    }
//
//    detekt {
//        allRules = false
//        autoCorrect = true
//        buildUponDefaultConfig = true
//        baseline = file("$rootDir/config/detekt/baseline.xml")
//        config.setFrom("$rootDir/config/detekt/detekt.yml")
//        source.setFrom(
//            "src/commonMain/kotlin",
//            "src/commonMain/kotlin",
//            "src/jvmMain/kotlin",
//            "src/jvmTest/kotlin",
//            "src/nativeMain/kotlin",
//            "src/nativeTest/kotlin",
//        )
//    }
//}

tasks {
    wrapper {
        gradleVersion = "8.13"
    }
}
