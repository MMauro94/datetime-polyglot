import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.js-plain-objects")
}

group = rootProject.group
val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
    }
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()
    explicitApi()

    jvm()
    android {
        namespace = "dev.mmauro.datetimepolyglot"
        compileSdk = libs.findVersion("android.compileSdk").get().toString().toInt()
        minSdk = libs.findVersion("android.minSdk").get().toString().toInt()

        withJava() // enable java compilation support
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        packaging {
            resources {
                excludes += "META-INF/AL2.0"
                excludes += "META-INF/LGPL2.1"
                excludes += "META-INF/LICENSE.md"
                excludes += "META-INF/LICENSE-notice.md"
            }
        }
    }
    js {
        nodejs()
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
        compilerOptions {
        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
    }

    applyDefaultHierarchyTemplate()
}

project.plugins.withType<NodeJsPlugin> {
    project.the<NodeJsEnvSpec>().version = "26.2.0"
}
