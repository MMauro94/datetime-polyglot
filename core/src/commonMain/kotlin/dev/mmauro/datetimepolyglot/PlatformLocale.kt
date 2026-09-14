package dev.mmauro.datetimepolyglot

public expect class PlatformLocale

/**
 * Returns the current default locale (i.e. system locale)
 */
public expect fun getDefaultLocale(): PlatformLocale

/**
 * Returns the [PlatformLocale] corresponding to the given Bcp47 [tag].
 */
public expect fun getLocaleFromTag(tag: String): PlatformLocale
