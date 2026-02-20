plugins { id("com.bazaar.build") }

kotlin {
    sourceSets {
        commonMain { dependencies { implementation(projects.bazaarParser) } }
        commonTest { dependencies { implementation(kotlin("test")) } }
    }
}
