package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private val MONDAY_DATE = LocalDate(1970, 1, 5)

data class DayOfWeekOptions(
    val style: DayOfWeekStyle = DayOfWeekStyle.WIDE,
)

expect class DayOfWeekLocalizer(
    locale: PlatformLocale = getDefaultLocale(),
    options: DayOfWeekOptions = DayOfWeekOptions(),
) : DateTimeLocalizer<DayOfWeek> {
    override fun localize(value: DayOfWeek): String
}

fun DayOfWeek.localize(
    locale: PlatformLocale = getDefaultLocale(),
    options: DayOfWeekOptions = DayOfWeekOptions(),
) = DayOfWeekLocalizer(locale, options).localize(this)

internal fun DayOfWeek.toArbitraryLocalDate() = MONDAY_DATE + DatePeriod(days = this.ordinal)
