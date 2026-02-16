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
                        }
                }
        }
}

tasks.withType<Test> {
        systemProperty("record", System.getProperty("record") ?: "false")
        systemProperty("testdata.dir",
                project.file("src/jvmTest/resources/testdata").absolutePath)
}
