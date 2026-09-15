pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("kotlinWrappers") {
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:2026.5.6")
        }
    }
}

// For some reason Dokka doesn't like it when a module is called exactly like the root project and does not include the submodule in the
// aggregated documentation. Adding `-root` suffix for now as a workaround.
rootProject.name = "datetime-polyglot-root"
include(
    ":core",
    ":datetime-polyglot",
    ":compose",
)
