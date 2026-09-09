import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.GenerateCldrCodeTask

plugins {
    id("datetime-polyglot.common")
}

val generateCldrCodeTask = tasks.register<GenerateCldrCodeTask>("generateCldrCode")

kotlin {
    sourceSets {
        // Create JVM+Android common source sets
        val jvmAndAndroidMain = create("jvmAndAndroidMain") {
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(jvmAndAndroidMain)
        jvmMain.get().dependsOn(jvmAndAndroidMain)

        val jvmAndAndroidTest = create("jvmAndAndroidTest") {
            dependsOn(commonTest.get())
        }
        getByName("androidDeviceTest").dependsOn(jvmAndAndroidTest)
        jvmTest.get().dependsOn(jvmAndAndroidTest)

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.datetime)
            api(project(":core"))
        }

        androidMain {
            kotlin.srcDir(generateCldrCodeTask.map { it.androidMainOutput })
            dependencies {
                implementation(libs.androidx.annotationJvm)
            }
        }

        jvmMain.dependencies {
            implementation(libs.ibm.icu4j)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.kotest.runner.junit4)
        }

        webMain {
            kotlin.srcDir(generateCldrCodeTask.map { it.webMainOutput })

            dependencies {
                implementation(kotlinWrappers.js)
                implementation(kotlinWrappers.jsPlainObject)
            }
        }

        webTest.dependencies {
            implementation(npm("@js-joda/timezone", "2.25.1"))
            implementation(npm("weekstart", "2.0.0"))
        }
    }
}

dokka {
    dokkaSourceSets {
        named("jvmAndAndroidMain") {
            displayName = "JVM + Android"
        }
    }
}
