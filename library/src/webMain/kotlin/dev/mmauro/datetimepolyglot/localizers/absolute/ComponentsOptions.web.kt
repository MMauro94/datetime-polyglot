package dev.mmauro.datetimepolyglot.localizers.absolute

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
import js.intl.DateTimeFormatOptions
import js.intl.TimeZoneNameFormat

internal fun DateTimeFormatOptions.fill(dateOptions: ComponentsOptions.Date) {
    when (dateOptions) {
        is ComponentsOptions.Date.Style -> {
            dateStyle = dateOptions.style.toJsDateStyle()
        }

        is ComponentsOptions.Date.Components -> {
            dateOptions.eraStyle?.let { era = it.toEraFormat() }
            dateOptions.yearStyle?.let { year = it.toYearFormat() }
            dateOptions.monthStyle?.let { month = it.toMonthFormat() }
            dateOptions.dayOfWeekStyle?.let { weekday = it.toWeekdayFormat() }
            dateOptions.dayOfMonthStyle?.let { day = it.toDayFormat() }
        }
    }
}

internal fun DateTimeFormatOptions.fill(timeOptions: ComponentsOptions.Time, timeZoneIdFallback: TimeZoneNameFormat? = null) {
    timeOptions.hourCycle?.let { hourCycle = it.toJsHourCycle() }

    when (val timeStyleOptions = timeOptions.styleOptions) {
        is ComponentsOptions.TimeStyleOptions.Style -> {
            timeStyle = timeStyleOptions.style.toJsTimeStyle()
        }

        is ComponentsOptions.TimeStyleOptions.Components -> {
            timeStyleOptions.dayPeriodStyle?.let { dayPeriod = it.toDayPeriodFormat() }
            timeStyleOptions.hourStyle?.let { hour = it.toHourFormat() }
            timeStyleOptions.minuteStyle?.let { minute = it.toMinuteFormat() }
            timeStyleOptions.secondStyle?.let { second = it.toSecondFormat() }
            if (timeStyleOptions.fractionalSecondDigits > 0) {
                fractionalSecondDigits = timeStyleOptions.fractionalSecondDigits
            }
            timeStyleOptions.timeZoneStyle?.let { timeZoneName = it.toTimeZoneNameFormat(idFallback = timeZoneIdFallback) }
        }
    }
}