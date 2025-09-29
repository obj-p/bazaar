dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

rootProject.name = "bazaar"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
