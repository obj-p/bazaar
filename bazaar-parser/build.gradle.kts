import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask

plugins {
        alias(libs.plugins.antlr.kotlin)
        id("com.bazaar.build")
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
        jvm()

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
                        }
                }
        }
}
