package dev.mmauro.datetimepolyglot.buildlogic.utils

import io.github.z4kn4fein.semver.Version

fun Version.Companion.fromTag(tag: String): Version {
    require(tag.startsWith("v")) { "tag '$tag' doesn't start with v" }
    return tag.trim().removePrefix("v").let(Version::parse)
}

