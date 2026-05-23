package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

data class ZonedInstantOptions(
    val eraStyle: EraStyle? = null,
    val yearStyle: YearStyle? = null,
    val monthStyle: MonthStyle? = null,
    val dayOfWeekStyle: DayOfWeekStyle? = null,
    val dayOfMonthStyle: DayOfMonthStyle? = null,
    val dayPeriodStyle: DayPeriodStyle? = null,
    val hourCycle: HourCycle? = null,
    val hourStyle: HourStyle? = null,
    val minuteStyle: MinuteStyle? = null,
    val secondStyle: SecondStyle? = null,
    val fractionalSecondDigits: Int = 0,
    val timezoneStyle: TimeZoneStyle? = null,
) {
    init {
        require(fractionalSecondDigits >= 0) { "fractionalSecondDigits cannot be negative" }
        require(
            eraStyle != null ||
                    yearStyle != null ||
                    monthStyle != null ||
                    dayOfWeekStyle != null ||
                    dayOfMonthStyle != null ||
                    hourStyle != null ||
                    minuteStyle != null ||
                    secondStyle != null ||
                    fractionalSecondDigits > 0 ||
                    timezoneStyle != null
        ) {
            "At least one component must be not-null"
        }

    }
}

expect class ZonedInstantLocalizer(
    locale: PlatformLocale = getDefaultLocale(),
    options: ZonedInstantOptions = ZonedInstantOptions(),
) : DateTimeZonedLocalizer<Instant> {
    override fun localize(value: Zoned<Instant>): String
}

fun Zoned<Instant>.localize(
    locale: PlatformLocale = getDefaultLocale(),
    options: ZonedInstantOptions = ZonedInstantOptions(),
) = ZonedInstantLocalizer(locale, options).localize(this)

fun Instant.localize(
    timezone: TimeZone,
    locale: PlatformLocale = getDefaultLocale(),
    options: ZonedInstantOptions = ZonedInstantOptions(),
) = ZonedInstantLocalizer(locale, options).localize(Zoned(this, timezone))
