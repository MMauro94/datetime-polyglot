import io.github.z4kn4fein.semver.Version
import org.gradle.kotlin.dsl.register
import kotlin.collections.flatMap
import kotlin.collections.orEmpty
import kotlin.collections.toList

plugins {
    id("dev.mmauro.gitver")
    id("org.jetbrains.dokka")
}

group = "dev.mmauro.datetime-polyglot"
version = gitver.currentVersion.get()

val dokkaStorage = layout.projectDirectory.dir("dokka")
dokka {
    dokkaPublications.html {
        failOnWarning = true
        moduleName = "datetime-polyglot"
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
                                it.isDirectory && Version.parse(it.name) > gitver.currentVersion.get()
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
    dokka(project(":compose"))
}

tasks.register<Copy>("storeDokkaHtml") {
    description = "Moves the Dokka documentation to the Dokka storage folder"
    group = "dokka"

    from("build/dokka/html")

    val version = gitver.currentVersion.get()
    into(dokkaStorage.dir(version.preRelease?.lowercase() ?: "stable").dir(version.toString()))
}
