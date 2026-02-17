import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask

plugins {
        id("com.bazaar.build")
        alias(libs.plugins.antlr.kotlin)
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
        dependsOn("cleanGenerateKotlinGrammarSource")

        source = fileTree(layout.projectDirectory.dir("antlr")) {
                include("**/*.g4")
        }

        val pkgName = "com.bazaar.parser.antlr"
        packageName = pkgName
        arguments = listOf("-visitor")

        val outDir = "generatedAntlr/${pkgName.replace(".", "/")}"
        outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}

kotlin {
        sourceSets {
                commonMain {
                        kotlin {
                                srcDir(generateKotlinGrammarSource)
                        }

                        dependencies {
                                implementation(libs.antlr.kotlin.runtime)
                        }
                }

                commonTest {
                        dependencies {
                                implementation(kotlin("test"))
                                implementation(libs.kotlinx.io.core)
                        }
                }
        }
}

val testdataDir = project.file("src/commonTest/resources/testdata").absolutePath
val recordMode = System.getProperty("record") ?: System.getenv("RECORD") ?: "false"

tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest> {
        environment("TESTDATA_DIR", testdataDir)
        environment("RECORD", recordMode)
}
