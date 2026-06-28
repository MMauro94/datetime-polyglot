package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalDate

/**
 * Localizer for [LocalDate].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [LocalDate.localize] for one-off localizations.
 *
 * Examples:
 * - `1/8/26`
 * - `Jan 8, 2026`
 * - `January 8, 2026`
 * - `Thursday, January 8, 2026`
 */
// For now this constructor is private because it's useless for users to manually create a DateOptions instance
expect class LocalDateLocalizer private constructor(
    options: DateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<LocalDate> {

    constructor(options: DateStyleOptions, locale: PlatformLocale = getDefaultLocale())

    override fun localize(value: LocalDate): String
}

/**
 * Localizes this [LocalDate] with the given [options] in the given [locale].
 *
 * @see LocalDateLocalizer
 */
fun LocalDate.localize(
    options: DateStyleOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalDateLocalizer(options, locale).localize(this)
