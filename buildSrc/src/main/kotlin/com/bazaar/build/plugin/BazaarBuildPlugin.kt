package com.bazaar.build.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class BazaarBuildPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")

        target.extensions.configure<KotlinMultiplatformExtension>("kotlin") {
            jvm()
            macosArm64()
            macosX64()
            linuxX64()
            mingwX64()
        }
    }
}
