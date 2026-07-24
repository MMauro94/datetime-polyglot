package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalTime

/**
 * Localizer for [LocalTime].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [LocalTime.localize] for one-off localizations.
 *
 * Note that only the hour is required when using [LocalTimeComponents], all others are optional and can be omitted if so desired.
 * In case of a "gap", the underlying implementation will try return a string containing only the required fields, which can be odd or
 * unexpected in some locales. One example of such case is the last example below.
 *
 * Example:
 * - `9:05 PM`
 * - `9:05:08 PM`
 * - `21:05`
 * - `21:05:08.123`
 * - `9 at night`
 * - `9 PM (second: 8.1)`
 */
expect class LocalTimeLocalizer(
    options: LocalTimeOptions<*> = LocalTimeOptions(Defaults.LOCAL_TIME),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<LocalTime> {

    constructor(options: LocalTimeStyleOptions, locale: PlatformLocale = getDefaultLocale())

    override fun localize(value: LocalTime): String
}

/**
 * Localizes this [LocalTime] with the given [options] in the given [locale].
 *
 * @see LocalTimeLocalizer
 */
fun LocalTime.localize(
    options: LocalTimeOptions<*> = LocalTimeOptions(Defaults.LOCAL_TIME),
    locale: PlatformLocale = getDefaultLocale(),
) = LocalTimeLocalizer(options, locale).localize(this)

/**
 * Localizes this [LocalTime] with the given [options] in the given [locale].
 *
 * @see LocalTimeLocalizer
 */
fun LocalTime.localize(
    options: LocalTimeStyleOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalTimeLocalizer(options, locale).localize(this)

