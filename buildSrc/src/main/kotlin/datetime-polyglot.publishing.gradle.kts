plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    val artifactId = project.name.takeIf { it != "library" } ?: "datetime-polyglot"
    coordinates(rootProject.group.toString(), artifactId, rootProject.version.toString())

    pom {
        name = "datetime-polyglot:$artifactId"
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
