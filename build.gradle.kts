import dev.mmauro.datetimepolyglot.buildlogic.extensions.GitInfoExtension
import dev.mmauro.datetimepolyglot.buildlogic.tasks.PrintVersionTask
import io.github.z4kn4fein.semver.Version
import org.gradle.kotlin.dsl.register
import kotlin.collections.flatMap
import kotlin.collections.orEmpty
import kotlin.collections.toList

plugins {
    id("git-info")
    id("org.jetbrains.dokka")
}

val gitInfo = extensions.getByType(GitInfoExtension::class.java)

group = "dev.mmauro.datetime-polyglot"
version = gitInfo.currentVersion.get()

val dokkaStorage = layout.projectDirectory.dir("dokka")
dokka {
    dokkaPublications.html {
        failOnWarning = true
    }
    dokkaSourceSets {
        configureEach {
            externalDocumentationLinks.register("kotlinx-datetime") {
                url("https://kotlinlang.org/api/kotlinx-datetime/")
            }
            externalDocumentationLinks.register("kotlinx-coroutines") {
                url("https://kotlinlang.org/api/kotlinx.coroutines/")
            }
            sourceLink {
                localDirectory.set(file(rootDir))
                val ref = gitInfo.currentVersion.get().let { if (it.preRelease.equals("SNAPSHOT")) "main" else "v$it" }
                remoteUrl("https://github.com/MMauro94/datetime-polyglot/tree/$ref")
            }
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
                        folder
                            .listFiles {
                                it.isDirectory && Version.parse(it.name) > gitInfo.latestVersion.get()
                            }.orEmpty().toList()
                    },
            )

            // Do not store older version inside subdirectory when copying them over for final build
            olderVersionsDirName = ""
        }
    }
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.versioning)
    dokka(project(":datetime-polyglot"))
    dokka(project(":core"))
}

tasks.register<Copy>("storeDokkaHtml") {
    description = "Moves the Dokka documentation to the Dokka storage folder"
    group = "dokka"

    from("build/dokka/html")

    val version = gitInfo.currentVersion.get()
    into(dokkaStorage.dir(version.preRelease?.lowercase() ?: "stable").dir(version.toString()))
}

tasks.register<PrintVersionTask>("getCurrentVersion") {
    description = "Print the version tracket by the current commit"
    version = gitInfo.currentVersion
}
tasks.register<PrintVersionTask>("findLatestStableRelease") {
    description = "Print the latest stable version found in the repository"
    version = gitInfo.latestStableRelease
}
