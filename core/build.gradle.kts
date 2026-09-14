plugins {
    id("datetime-polyglot.common")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.datetime)
        }
        webMain.dependencies {
            implementation(kotlinWrappers.js)
        }
        webTest.dependencies {
            implementation(npm("@js-joda/timezone", "2.25.1"))
        }
    }
}
