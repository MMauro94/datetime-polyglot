plugins {
    id("datetime-polyglot.common")
    alias(libs.plugins.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.datetime)
            api(project(":datetime-polyglot"))
        }
        webMain.dependencies {
            implementation(kotlinWrappers.js)
        }
    }
}
