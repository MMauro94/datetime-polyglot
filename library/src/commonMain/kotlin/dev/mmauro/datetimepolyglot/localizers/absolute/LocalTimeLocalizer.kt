package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalTime

expect class LocalTimeLocalizer(
    options: LocalTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalTime> {

    constructor(options: TimeStyleOptions.Local, locale: PlatformLocale = getDefaultLocale())

    override fun localize(value: LocalTime): String
}

/**
 * Localizes this [LocalTime] with the given [options] in the given [locale].
 *
 * @see LocalTimeLocalizer
 */
fun LocalTime.localize(
    options: LocalTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalTimeLocalizer(options, locale).localize(this)

/**
 * Localizes this [LocalTime] with the given [options] in the given [locale].
 *
 * @see LocalTimeLocalizer
 */
fun LocalTime.localize(
    options: TimeStyleOptions.Local,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalTimeLocalizer(options, locale).localize(this)

