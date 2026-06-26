import dev.mmauro.datetimepolyglot.buildlogic.extensions.GitInfoExtension
import dev.mmauro.datetimepolyglot.buildlogic.tasks.PrintVersionTask
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
    alias(libs.plugins.dokka)
    id("dtp")
}

val artifactId = "datetime-polyglot"
group = "dev.mmauro"

val gitInfo = extensions.getByType(GitInfoExtension::class.java)

version = gitInfo.currentVersion.get().toString()

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
    }
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()

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
            implementation(libs.kotest.property)
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

tasks.register<PrintVersionTask>("getCurrentVersion") {
    description = "Print the version tracket by the current commit"
    version = gitInfo.currentVersion
}
tasks.register<PrintVersionTask>("findLatestStableRelease") {
    description = "Print the latest stable version found in the repository"
    version = gitInfo.latestStableRelease
}

project.plugins.withType<NodeJsPlugin> {
    project.the<NodeJsEnvSpec>().version = "26.2.0"
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.versioning)
}

val dokkaStorage = layout.projectDirectory.dir("../dokka")
dokka {
    dokkaPublications.html {
        failOnWarning = true
    }
    dokkaSourceSets {
        configureEach {
            externalDocumentationLinks.register("kotlinx-datetime") {
                url("https://kotlinlang.org/api/kotlinx-datetime/")
            }
            sourceLink {
                localDirectory.set(file(rootDir))
                val ref = gitInfo.currentVersion.get().let { if (it.preRelease.equals("SNAPSHOT")) "main" else "v$it" }
                remoteUrl("https://github.com/MMauro94/datetime-polyglot/tree/$ref")
            }
        }
        named("jvmAndAndroidMain") {
            displayName = "JVM + Android"
        }
    }
    pluginsConfiguration {
        versioning {
            // Add all stable versions
            olderVersionsDir = dokkaStorage.dir("stable")

            // Add all non-stable versions > current
            olderVersions.from(
                dokkaStorage
                    .asFile
                    .listFiles { it.isDirectory && it.name !in setOf("stable") && !it.name.startsWith(".") }
                    .orEmpty()
                    .flatMap { folder ->
                        folder.listFiles {
                            it.isDirectory && Version.parse(it.name) > gitInfo.latestVersion.get()
                        }.toList()
                    }
            )

            // Do not store older version inside subdirectory when copying them over for final build
            olderVersionsDirName = ""
        }
    }
}

tasks.register<Copy>("storeDokkaHtml") {
    description = "Moves the Dokka documentation to the Dokka storage folder"
    group = "dokka"

    from("build/dokka/html")

    val version = gitInfo.currentVersion.get()
    into(dokkaStorage.dir(version.preRelease?.lowercase() ?: "stable").dir(version.toString()))
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
