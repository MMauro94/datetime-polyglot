package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.styles.toDayFormat
import dev.mmauro.datetimepolyglot.styles.toDayPeriodFormat
import dev.mmauro.datetimepolyglot.styles.toEraFormat
import dev.mmauro.datetimepolyglot.styles.toHourFormat
import dev.mmauro.datetimepolyglot.styles.toMinuteFormat
import dev.mmauro.datetimepolyglot.styles.toMonthFormat
import dev.mmauro.datetimepolyglot.styles.toSecondFormat
import dev.mmauro.datetimepolyglot.styles.toTimeZoneNameFormat
import dev.mmauro.datetimepolyglot.styles.toWeekdayFormat
import dev.mmauro.datetimepolyglot.styles.toYearFormat
import dev.mmauro.datetimepolyglot.toJsHourCycle
import dev.mmauro.datetimepolyglot.toJsInstant
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlin.math.max
import kotlin.time.Instant

internal actual class ComponentsLocalizer actual constructor(
    private val locale: PlatformLocale,
    private val options: ComponentsOptions
) : DateTimeZonedLocalizer<Instant> {

    actual override fun localize(value: Zoned<Instant>): String {
        val (instant, timezone) = value

        val formatOptions: DateTimeFormatOptions = unsafeJso {
            timeZone = timezone.id
            options.hourCycle?.let { hourCycle = it.toJsHourCycle() }

            when (val dateOptions = options.dateOptions) {
                is ComponentsOptions.DateOptions.Style -> {
                    dateStyle = dateOptions.style.toJsDateStyle()
                }

                is ComponentsOptions.DateOptions.Components -> {
                    dateOptions.eraStyle?.let { era = it.toEraFormat() }
                    dateOptions.yearStyle?.let { year = it.toYearFormat() }
                    dateOptions.monthStyle?.let { month = it.toMonthFormat() }
                    dateOptions.dayOfWeekStyle?.let { weekday = it.toWeekdayFormat() }
                    dateOptions.dayOfMonthStyle?.let { day = it.toDayFormat() }
                }

                null -> {}
            }

            when (val timeOptions = options.timeOptions) {
                is ComponentsOptions.TimeOptions.Style -> {
                    timeStyle = timeOptions.style.toJsTimeStyle()
                }

                is ComponentsOptions.TimeOptions.Components -> {
                    timeOptions.dayPeriodStyle?.let { dayPeriod = it.toDayPeriodFormat() }
                    timeOptions.hourStyle?.let { hour = it.toHourFormat() }
                    timeOptions.minuteStyle?.let { minute = it.toMinuteFormat() }
                    timeOptions.secondStyle?.let { second = it.toSecondFormat() }
                    if (timeOptions.fractionalSecondDigits > 0) {
                        fractionalSecondDigits = max(timeOptions.fractionalSecondDigits, 3)
                    }
                    timeOptions.timezoneStyle?.let { timeZoneName = it.toTimeZoneNameFormat() }

                }

                null -> {}
            }
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(instant.toJsInstant())
    }
}