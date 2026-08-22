package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import kotlinx.datetime.LocalDateTime

/**
 * Localization options for [LocalDateTimeLocalizer] and [LocalDateTime.parse].
 */
public data class LocalDateTimeOptions private constructor(
    val dateOptions: DateOptions,
    val timeOptions: LocalTimeOptions<*>,
) : PolyglotLocalizerOptions<LocalDateTimeLocalizer> {

    public constructor(
        dateOptions: DateStyleOptions = Defaults.DATE,
        timeOptions: LocalTimeOptions<*> = LocalTimeOptions(Defaults.LOCAL_TIME),
    ) : this(DateOptions(dateOptions), timeOptions)

    public constructor(
        dateOptions: DateStyleOptions = Defaults.DATE,
        timeOptions: LocalTimeStyleOptions,
    ) : this(dateOptions, LocalTimeOptions(timeOptions))

    override fun localizer(locale: PlatformLocale): LocalDateTimeLocalizer = LocalDateTimeLocalizer(this, locale)
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
public expect class LocalDateTimeLocalizer(
    options: LocalDateTimeOptions = LocalDateTimeOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<LocalDateTime> {

    override fun localize(value: LocalDateTime): String
}

/**
 * Localizes this [LocalDateTime] with the given [options] in the given [locale].
 *
 * @see LocalDateTimeLocalizer
 */
public fun LocalDateTime.localize(
    options: LocalDateTimeOptions = LocalDateTimeOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): String = LocalDateTimeLocalizer(options, locale).localize(this)
