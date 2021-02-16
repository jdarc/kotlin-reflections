plugins {
    kotlin("js") version "1.4.30"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
                outputFileName = "main.js"
            }
            runTask {
                cssSupport.enabled = true
                outputFileName = "main.js"
            }
        }
    }
}

