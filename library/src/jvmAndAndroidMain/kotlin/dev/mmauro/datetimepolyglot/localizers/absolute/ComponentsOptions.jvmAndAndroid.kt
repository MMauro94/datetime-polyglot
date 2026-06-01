package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateFormat
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDateFormatForSkeleton
import dev.mmauro.datetimepolyglot.getDateFormatInstance
import dev.mmauro.datetimepolyglot.getDateTimeFormatInstance
import dev.mmauro.datetimepolyglot.getTimeFormatInstance
import dev.mmauro.datetimepolyglot.styles.fractionalSecondsUnicodePattern
import dev.mmauro.datetimepolyglot.styles.unicodePattern
import dev.mmauro.datetimepolyglot.styles.unicodeSkeleton
import dev.mmauro.datetimepolyglot.withHourCycle

internal fun ComponentsOptions.toDateFormat(locale: PlatformLocale): DateFormat {
    // Optimizations to avoid going through getDateFormatForSkeleton needlessly
    val hourCycle = timeOptions?.hourCycle
    val localeWithHourCycle = if (hourCycle == null) locale else locale.withHourCycle(hourCycle)
    return when (dateOptions) {
        is ComponentsOptions.DateOptions.Style if timeOptions is ComponentsOptions.TimeOptions.Style -> {
            getDateTimeFormatInstance(dateOptions.style, timeOptions.style, localeWithHourCycle)
        }

        is ComponentsOptions.DateOptions.Style if timeOptions == null -> {
            getDateFormatInstance(dateOptions.style, locale)
        }

        null if timeOptions is ComponentsOptions.TimeOptions.Style -> {
            getTimeFormatInstance(timeOptions.style, localeWithHourCycle)
        }

        // Optimizations fail, fallback to normal case
        else -> {
            val dateSkeleton = when (dateOptions) {
                is ComponentsOptions.DateOptions.Style -> listOf(getDateFormatInstance(dateOptions.style, locale).toPattern())
                is ComponentsOptions.DateOptions.Components -> listOfNotNull(
                    dateOptions.eraStyle?.unicodePattern,
                    dateOptions.yearStyle?.unicodePattern,
                    dateOptions.monthStyle?.unicodePattern,
                    dateOptions.dayOfWeekStyle?.unicodePattern,
                    dateOptions.dayOfMonthStyle?.unicodePattern,
                )

                null -> emptyList()
            }

            val timeSkeleton = when (val timeOptions = timeOptions) {
                is ComponentsOptions.TimeOptions.Style -> listOf(getTimeFormatInstance(timeOptions.style, localeWithHourCycle).toPattern())
                is ComponentsOptions.TimeOptions.Components -> listOfNotNull(
                    timeOptions.hourStyle?.unicodeSkeleton(locale, timeOptions.dayPeriodStyle, hourCycle),
                    timeOptions.minuteStyle?.unicodePattern,
                    timeOptions.secondStyle?.unicodePattern,
                    fractionalSecondsUnicodePattern(timeOptions.fractionalSecondDigits),
                    timeOptions.timeZoneStyle?.unicodePattern,
                )

                null -> emptyList()
            }

            val skeleton = (dateSkeleton + timeSkeleton).joinToString(separator = " ")
            getDateFormatForSkeleton(skeleton, locale)
        }
    }
}