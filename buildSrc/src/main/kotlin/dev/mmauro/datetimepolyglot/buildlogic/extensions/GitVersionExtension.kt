package dev.mmauro.datetimepolyglot.buildlogic.extensions

import dev.mmauro.datetimepolyglot.buildlogic.utils.fromTag
import io.github.z4kn4fein.semver.Version
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

interface GitVersionExtension {
    /**
     * Last version that is reachable from HEAD
     */
    val last: Provider<Version>

    /**
     * Version for the currently checked out commit (HEAD).
     */
    val current: Provider<Version>

    /**
     * List of **all** versions present in the repo, include pre-releases like betas, etc.
     *
     * Doesn't include SNAPSHOTs because those are not tagged.
     */
    val versions: Provider<List<Version>>

    /**
     * Latest **stable** version that exists in the repo (i.e. excludes alpha, betas, RCs, etc.)
     */
    val latestStable: Provider<Version>
}

class DefaultGitVersionExtension(
    private val providers: ProviderFactory,
) : GitVersionExtension {

    override val last = providers.exec {
        commandLine("git", "describe", "--tags", "--abbrev=0", "--match", "v*")
    }.standardOutput.asText.map { Version.fromTag(it) }

    override val current = providers.exec {
        commandLine("git", "tag", "--points-at", "HEAD", "v*")
    }.standardOutput.asText.map { stdout ->
        val versions = stdout.split("\n").filter { it.isNotEmpty() }.map { Version.fromTag(it) }
        check(versions.size <= 1) { "too many versions pointing at HEAD commit ($versions)" }

        val current = versions.singleOrNull()
        val latest = last.get()

        when (current) {
            null -> latest.copy(patch = latest.patch + 1, preRelease = "SNAPSHOT")
            latest -> latest
            else -> error("Impossible state: current version commit ($current) is different than latest ($latest)")
        }
    }

    override val versions = providers
        .exec { commandLine("git", "tag", "--merged", "HEAD", "v*") }
        .standardOutput
        .asText
        .map { stdout ->
            stdout
                .split("\n")
                .filter { it.isNotEmpty() }
                .map { Version.fromTag(it) }
                .sorted()
        }

    override val latestStable = providers.provider {
        versions.get()
            .filter { it.preRelease == null }
            .max()
    }
}
