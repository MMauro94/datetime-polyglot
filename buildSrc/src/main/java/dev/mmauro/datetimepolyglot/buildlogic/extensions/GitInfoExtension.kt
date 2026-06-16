package dev.mmauro.datetimepolyglot.buildlogic.extensions

import dev.mmauro.datetimepolyglot.buildlogic.utils.fromTag
import io.github.z4kn4fein.semver.Version
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import kotlin.text.ifEmpty

interface GitInfoExtension {
    val latestVersion: Provider<Version>
    val currentVersion: Provider<Version>
}

class DefaultGitInfoExtension(
    private val providers: ProviderFactory
) : GitInfoExtension {

    // TODO remove ifEmpty/isIgnoreExitValue when we have at least one version
    override val latestVersion = providers.exec {
        commandLine("git", "describe", "--tags", "--abbrev=0", "--match", "v*")
        isIgnoreExitValue = true
    }.standardOutput.asText.map { Version.fromTag(it.ifEmpty { "v0.0.0" }) }

    override val currentVersion = providers.exec {
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
}