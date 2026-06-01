package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalDate

expect class LocalDateLocalizer(
    options: DateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalDate> {

    override fun localize(value: LocalDate): String
}

/**
 * Localizes this [LocalDate] with the given [options] in the given [locale].
 *
 * @see LocalDateLocalizer
 */
fun LocalDate.localize(
    options: DateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalDateLocalizer(options, locale).localize(this)

internal fun DateOptions.toComponentOptions(): ComponentsOptions.DateOptions {
    return when (this) {
        is DateStyle -> ComponentsOptions.DateOptions.Style(this)
        is DateComponents -> ComponentsOptions.DateOptions.Components(
            eraStyle = eraStyle,
            yearStyle = yearStyle,
            monthStyle = monthStyle,
            dayOfMonthStyle = dayOfMonthStyle,
            dayOfWeekStyle = dayOfWeekStyle,
        )
    }
}