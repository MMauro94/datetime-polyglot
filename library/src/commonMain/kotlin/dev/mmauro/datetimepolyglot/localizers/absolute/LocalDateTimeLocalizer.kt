package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalDateTime

expect class LocalDateTimeLocalizer(
    options: LocalDateTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalDateTime> {

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
