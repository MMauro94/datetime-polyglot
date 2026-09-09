import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("io.kotest")
    id("dev.mokkery")
}

tasks.named<Test>("jvmTest").configure {
    useJUnitPlatform()
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.findLibrary("kotlinx-coroutines-test").get())
            implementation(libs.findLibrary("kotest-framework").get())
            implementation(libs.findLibrary("kotest-assertions-core").get())
            implementation(libs.findLibrary("kotest-property").get())
        }

        jvmTest.dependencies {
            implementation(libs.findLibrary("kotest-runner-junit5").get())
        }
    }
}
