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

internal fun ComponentsOptions.toDateFormat(locale: PlatformLocale): DateFormat {
    // Optimizations to avoid going through getDateFormatForSkeleton needlessly
    val hourCycle = timeOptions?.hourCycle
    return when (dateOptions) {
        is ComponentsOptions.Date.Style if timeOptions?.styleOptions is ComponentsOptions.TimeStyleOptions.Style -> {
            val timeOptions = TimeOptions(
                styleOptions = timeOptions.styleOptions.style,
                hourCycle = hourCycle,
            )
            getDateTimeFormatInstance(dateOptions.style, timeOptions, locale)
        }

        is ComponentsOptions.Date.Style if timeOptions == null -> {
            getDateFormatInstance(dateOptions.style, locale)
        }

        null if timeOptions?.styleOptions is ComponentsOptions.TimeStyleOptions.Style -> {
            val timeOptions = TimeOptions(
                styleOptions = timeOptions.styleOptions.style,
                hourCycle = hourCycle,
            )
            getTimeFormatInstance(timeOptions, locale)
        }

        // Optimizations fail, fallback to normal case
        else -> {
            val dateSkeleton = when (dateOptions) {
                is ComponentsOptions.Date.Style -> listOf(getDateFormatInstance(dateOptions.style, locale).toPattern())
                is ComponentsOptions.Date.Components -> listOfNotNull(
                    dateOptions.eraStyle?.unicodePattern,
                    dateOptions.yearStyle?.unicodePattern,
                    dateOptions.monthStyle?.unicodePattern,
                    dateOptions.dayOfWeekStyle?.unicodePattern,
                    dateOptions.dayOfMonthStyle?.unicodePattern,
                )

                null -> emptyList()
            }

            val timeSkeleton = when (val timeStyleOptions = timeOptions?.styleOptions) {
                is ComponentsOptions.TimeStyleOptions.Style -> {
                    val timeOptions = TimeOptions(
                        styleOptions = timeOptions.styleOptions.style,
                        hourCycle = hourCycle,
                    )
                    listOf(getTimeFormatInstance(timeOptions, locale).toPattern())
                }
                is ComponentsOptions.TimeStyleOptions.Components -> listOfNotNull(
                    timeStyleOptions.hourStyle?.unicodeSkeleton(locale, timeStyleOptions.dayPeriodStyle, hourCycle),
                    timeStyleOptions.minuteStyle?.unicodePattern,
                    timeStyleOptions.secondStyle?.unicodePattern,
                    fractionalSecondsUnicodePattern(timeStyleOptions.fractionalSecondDigits),
                    timeStyleOptions.timeZoneStyle?.unicodePattern,
                )

                null -> emptyList()
            }

            val skeleton = (dateSkeleton + timeSkeleton).joinToString(separator = " ")
            getDateFormatForSkeleton(skeleton, locale)
        }
    }
}