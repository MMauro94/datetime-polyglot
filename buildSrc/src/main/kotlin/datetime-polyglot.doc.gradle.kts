import dev.mmauro.datetimepolyglot.buildlogic.extensions.GitVersionExtension

plugins {
    id("org.jetbrains.dokka")
    id("datetime-polyglot.versioning")
}

val gitVersion = extensions.getByType(GitVersionExtension::class.java)

dokka {
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
                val ref = gitVersion.current.get().let { if (it.preRelease.equals("SNAPSHOT")) "main" else "v$it" }
                remoteUrl("https://github.com/MMauro94/datetime-polyglot/tree/$ref")
            }
        }
    }
}
