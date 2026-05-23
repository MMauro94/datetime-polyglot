package dev.mmauro.datetimepolyglot.localizers.component

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private val DEFAULT_STYLE = DayOfWeekStyle.WIDE
private val MONDAY_DATE = LocalDate(1970, 1, 5)

enum class DayOfWeekStyle {
    /**
     * Single letter, e.g. `T`
     */
    NARROW,

    /**
     * Two letters, e.g. `Tu`
     *
     * WARNING: Might not be supported by all targets, will fall back to [ABBREVIATED] in such cases.
     */
    SHORT,

    /**
     * Three letters, e.g. `Tue`
     */
    ABBREVIATED,

    /**
     * Full name, e.g. `Tuesday`
     */
    WIDE,
}

data class DayOfWeekOptions(
    val style: DayOfWeekStyle = DEFAULT_STYLE,
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
