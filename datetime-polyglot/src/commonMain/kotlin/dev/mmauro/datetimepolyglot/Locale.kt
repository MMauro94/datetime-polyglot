package dev.mmauro.datetimepolyglot

import kotlinx.datetime.DayOfWeek

internal expect val LOCALE_ENGLISH: PlatformLocale

public expect class PlatformLocale

internal expect fun getDefaultLocale(): PlatformLocale

internal expect val PlatformLocale.bcp47LanguageTag: String

internal expect val PlatformLocale.baseLocale: PlatformLocale

internal expect fun localeFromBcp47LanguageTag(tag: String): PlatformLocale

internal expect val PlatformLocale.firstDayOfWeek: DayOfWeek

/**
 * Implements [RFC 4647, section 3.4](https://datatracker.ietf.org/doc/html/rfc4647#section-3.4)'s algorithm to look up locales, returning
 * a [Sequence] from best match to worst match.
 *
 * Note: this locale is NOT returned as part of the output sequence. E.g. for the locale "en" this will return an empty sequence
 */
internal fun PlatformLocale.rfc4647Lookup(): Sequence<PlatformLocale> = sequence {
    var last = bcp47LanguageTag
    while ('-' in last) {
        last = last.substringBeforeLast(delimiter = '-', missingDelimiterValue = "")
        if (last.isNotEmpty()) {
            // If we ended on a singleton, remove it
            if (last.substringAfterLast('-', missingDelimiterValue = "").length == 1) {
                last = last.substringBeforeLast(delimiter = '-')
            }

            yield(localeFromBcp47LanguageTag(last))
        }
    }
}
