package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalDateTime

/**
 * Localization options for [LocalDateTimeLocalizer] and [LocalDateTime.parse].
 */
data class LocalDateTimeOptions private constructor(
    val dateOptions: DateOptions,
    val timeOptions: LocalTimeOptions,
) {
    constructor(dateOptions: DateStyleOptions, timeOptions: LocalTimeOptions) : this(DateOptions(dateOptions), timeOptions)
    constructor(dateOptions: DateStyleOptions, timeOptions: TimeStyleOptions.Local) : this(dateOptions, LocalTimeOptions(timeOptions))
}

/**
 * Localizer for [LocalDateTime].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [LocalDateTime.localize] for one-off localizations.
 *
 * Note that the same warning in [LocalTimeLocalizer] for "gaps" in the time components applies here.
 *
 * Examples:
 * - `1/8/26 9:05 PM`
 * - `Jan 8, 2026, 9 at night`
 * - `January 8, 2026 at 9:31:45 PM`
 * - `Thursday, January 8, 2026 at 21:05`
 */
expect class LocalDateTimeLocalizer(
    options: LocalDateTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<LocalDateTime> {

    override fun localize(value: LocalDateTime): String
}

/**
 * Localizes this [LocalDateTime] with the given [options] in the given [locale].
 *
 * @see LocalDateTimeLocalizer
 */
fun LocalDateTime.localize(
    options: LocalDateTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalDateTimeLocalizer(options, locale).localize(this)
