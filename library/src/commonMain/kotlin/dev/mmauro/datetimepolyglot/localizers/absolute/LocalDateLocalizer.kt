package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.absolute.ComponentsOptions.DateOptions.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

class LocalDateLocalizer(
    options: DateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalDate> {

    private val componentsLocalizer = ComponentsLocalizer(
        locale = locale,
        options = when (options) {
            is DateStyle -> ComponentsOptions(dateOptions = Style(options))
            is DateComponents -> ComponentsOptions(
                dateOptions = Components(
                    eraStyle = options.eraStyle,
                    yearStyle = options.yearStyle,
                    monthStyle = options.monthStyle,
                    dayOfMonthStyle = options.dayOfMonthStyle,
                    dayOfWeekStyle = options.dayOfWeekStyle,
                )
            )
        }
    )

    override fun localize(value: LocalDate): String {
        return componentsLocalizer.localize(Zoned(value.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC))
    }
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