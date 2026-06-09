package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalDate

// For now this constructor is private because it's useless for users to manually create a DateOptions instance
expect class LocalDateLocalizer private constructor(
    options: DateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalDate> {

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
