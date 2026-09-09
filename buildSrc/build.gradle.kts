plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.semver)
    implementation(libs.kotlinpoet)
    implementation(libs.ibm.icu4j)
    implementation(libs.kotlinx.datetime)

    // Plugins
    implementation(plugin(libs.plugins.kotlinMultiplatform))
    implementation(plugin(libs.plugins.android.kotlin.multiplatform.library))
    implementation(plugin(libs.plugins.vanniktech.mavenPublish))
    implementation(plugin(libs.plugins.js.plain.objects))
    implementation(plugin(libs.plugins.kotest))
    implementation(plugin(libs.plugins.ksp))
    implementation(plugin(libs.plugins.dokka))
    implementation(plugin(libs.plugins.mokkery))
    implementation(plugin(libs.plugins.ktlint))
}

gradlePlugin {
    plugins {
        create("git-info") {
            id = "git-info"
            implementationClass = "dev.mmauro.datetimepolyglot.buildlogic.GitInfoPlugin"
        }
    }
}

// Helper function that transforms a Gradle Plugin alias from a
// Version Catalog into a valid dependency notation for buildSrc
// https://docs.gradle.org/current/userguide/version_catalogs.html#sec:buildsrc-version-catalog
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
