package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.styles.toDayFormat
import dev.mmauro.datetimepolyglot.styles.toDayPeriodFormat
import dev.mmauro.datetimepolyglot.styles.toMonthFormat
import dev.mmauro.datetimepolyglot.styles.toEraFormat
import dev.mmauro.datetimepolyglot.styles.toHourFormat
import dev.mmauro.datetimepolyglot.styles.toMinuteFormat
import dev.mmauro.datetimepolyglot.styles.toSecondFormat
import dev.mmauro.datetimepolyglot.styles.toTimeZoneNameFormat
import dev.mmauro.datetimepolyglot.styles.toYearFormat
import dev.mmauro.datetimepolyglot.styles.toWeekdayFormat
import dev.mmauro.datetimepolyglot.toJsHourCycle
import dev.mmauro.datetimepolyglot.toJsInstant
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlin.math.max
import kotlin.time.Instant

actual class ZonedInstantLocalizer actual constructor(
    private val locale: PlatformLocale,
    private val options: ZonedInstantOptions
) : DateTimeZonedLocalizer<Instant> {

    actual override fun localize(value: Zoned<Instant>): String {
        val (instant, timezone) = value

        val formatOptions: DateTimeFormatOptions = unsafeJso {
            timeZone = timezone.id

            options.eraStyle?.let { era = it.toEraFormat() }
            options.yearStyle?.let { year = it.toYearFormat() }
            options.monthStyle?.let { month = it.toMonthFormat() }
            options.dayOfWeekStyle?.let { weekday = it.toWeekdayFormat() }
            options.dayOfMonthStyle?.let { day = it.toDayFormat() }
            options.dayPeriodStyle?.let { dayPeriod = it.toDayPeriodFormat() }
            options.hourCycle?.let { hourCycle = it.toJsHourCycle() }
            options.hourStyle?.let { hour = it.toHourFormat() }
            options.minuteStyle?.let { minute = it.toMinuteFormat() }
            options.secondStyle?.let { second = it.toSecondFormat() }
            if (options.fractionalSecondDigits > 0) {
                fractionalSecondDigits = max(options.fractionalSecondDigits, 3)
            }
            options.timezoneStyle?.let { timeZoneName = it.toTimeZoneNameFormat() }
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(instant.toJsInstant())
    }
}