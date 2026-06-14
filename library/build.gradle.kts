import io.github.z4kn4fein.semver.Version
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.js.plain.objects)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ksp)
}

val artifactId = "datetime-polyglot"
group = "dev.mmauro"

fun Version.Companion.fromTag(tag: String): Version {
    require(tag.startsWith("v")) { "tag '$tag' doesn't start with v" }
    return tag.trim().removePrefix("v").let(Version::parse)
}

val latestVersion = providers.exec {
    commandLine("git", "describe", "--tags", "--abbrev=0", "--match", "v*")
    isIgnoreExitValue = true
}.standardOutput.asText.map { Version.fromTag(it.ifEmpty { "v0.0.0" }) } // TODO remove ifEmpty when we have at least one version

val currentVersion = providers.exec {
    commandLine("git", "tag", "--points-at", "HEAD", "v*")
}.standardOutput.asText.map { stdout ->
    val versions = stdout.split("\n").filter { it.isNotEmpty() }.map { Version.fromTag(it) }
    check(versions.size <= 1) { "too many versions pointing at HEAD commit ($versions)" }

    val current = versions.singleOrNull()
    val latest = latestVersion.get()

    when (current) {
        null -> latest.copy(patch = latest.patch + 1, preRelease = "SNAPSHOT")
        latest -> latest
        else -> error("Impossible state: current version commit ($current) is different than latest ($latest)")
    }
}

version = currentVersion.get().toString()

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
    }
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled = true
    }

    jvm()
    androidLibrary {
        namespace = "dev.mmauro.datetimepolyglot"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

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

    sourceSets {
        // Create JVM+Android common source sets
        val jvmAndAndroidMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(jvmAndAndroidMain)
        jvmMain.get().dependsOn(jvmAndAndroidMain)

        val jvmAndAndroidTest by creating {
            dependsOn(commonTest.get())
        }
        getByName("androidDeviceTest").dependsOn(jvmAndAndroidTest)
        jvmTest.get().dependsOn(jvmAndAndroidTest)


        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotest.framework)
            implementation(libs.kotest.assertions.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.annotationJvm)
        }

        jvmMain.dependencies {
            implementation(libs.ibm.icu4j)
        }

        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.kotest.runner.junit4)
        }

        webMain.dependencies {
            implementation(kotlinWrappers.js)
            implementation(kotlinWrappers.jsPlainObject)
        }

        webTest.dependencies {
            implementation(npm("@js-joda/timezone", "2.25.1"))
        }
    }
}

tasks.named<Test>("jvmTest").configure {
    useJUnitPlatform()
}

project.plugins.withType<NodeJsPlugin> {
    project.the<NodeJsEnvSpec>().version = "26.2.0"
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), artifactId, version.toString())

    pom {
        name = artifactId
        description = " Multiplatform localization library for Kotlin date/time."
        inceptionYear = "2026"
        url = "https://github.com/MMauro94/datetime-polyglot/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "MMauro94"
                name = "Mauro Molin"
                email = "molin.mauro@gmail.com"
                url = "https://github.com/MMauro94"
                organization = "Mauro94"
                organizationUrl = "https://github.com/Mauro94"
            }
        }
        scm {
            url = "https://github.com/MMauro94/datetime-polyglot"
            connection = "scm:git:git://github.com/MMauro94/datetime-polyglot.git"
            developerConnection = "scm:git:ssh://github.com:MMauro94/datetime-polyglot.git"
        }
    }
}
