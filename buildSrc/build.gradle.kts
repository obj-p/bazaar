plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("bazaarBuild") {
            id = "com.bazaar.build"
            implementationClass = "com.bazaar.build.plugin.BazaarBuildPlugin"
        }
    }
}
