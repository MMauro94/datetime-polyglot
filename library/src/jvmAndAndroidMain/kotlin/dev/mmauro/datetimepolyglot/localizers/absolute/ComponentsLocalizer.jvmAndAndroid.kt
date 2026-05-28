package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateFormat
import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.format
import dev.mmauro.datetimepolyglot.getDateFormatForSkeleton
import dev.mmauro.datetimepolyglot.getDateFormatInstance
import dev.mmauro.datetimepolyglot.getDateTimeFormatInstance
import dev.mmauro.datetimepolyglot.getTimeFormatInstance
import dev.mmauro.datetimepolyglot.styles.fractionalSecondsUnicodePattern
import dev.mmauro.datetimepolyglot.styles.unicodePattern
import dev.mmauro.datetimepolyglot.styles.unicodeSkeleton
import dev.mmauro.datetimepolyglot.withHourCycle
import kotlinx.datetime.toJavaZoneId
import kotlin.time.Instant
import kotlin.time.toJavaInstant

internal actual class ComponentsLocalizer actual constructor(
    locale: PlatformLocale,
    options: ComponentsOptions,
) : DateTimeZonedLocalizer<Instant> {

    private val dateFormat: DateFormat = run {
        // Optimizations to avoid going through getDateFormatForSkeleton needlessly
        val localeWithHourCycle = if (options.hourCycle == null) locale else locale.withHourCycle(options.hourCycle)
        when (options.dateOptions) {
            is ComponentsOptions.DateOptions.Style if options.timeOptions is ComponentsOptions.TimeOptions.Style -> {
                getDateTimeFormatInstance(options.dateOptions.style, options.timeOptions.style, localeWithHourCycle)
            }

            is ComponentsOptions.DateOptions.Style if options.timeOptions == null -> {
                getDateFormatInstance(options.dateOptions.style, locale)
            }

            null if options.timeOptions is ComponentsOptions.TimeOptions.Style -> {
                getTimeFormatInstance(options.timeOptions.style, localeWithHourCycle)
            }

            // Optimizations fail, fallback to normal case
            else -> {
                val dateSkeleton = when (options.dateOptions) {
                    is ComponentsOptions.DateOptions.Style -> listOf(getDateFormatInstance(options.dateOptions.style, locale).toPattern())
                    is ComponentsOptions.DateOptions.Components -> listOfNotNull(
                        options.dateOptions.eraStyle?.unicodePattern,
                        options.dateOptions.yearStyle?.unicodePattern,
                        options.dateOptions.monthStyle?.unicodePattern,
                        options.dateOptions.dayOfWeekStyle?.unicodePattern,
                        options.dateOptions.dayOfMonthStyle?.unicodePattern,
                    )

                    null -> emptyList()
                }

                val timeSkeleton = when (val timeOptions = options.timeOptions) {
                    is ComponentsOptions.TimeOptions.Style -> listOf(getTimeFormatInstance(timeOptions.style, localeWithHourCycle).toPattern())
                    is ComponentsOptions.TimeOptions.Components -> listOf(
                        timeOptions.hourStyle?.unicodeSkeleton(locale, timeOptions.dayPeriodStyle, options.hourCycle),
                        timeOptions.minuteStyle?.unicodePattern,
                        timeOptions.secondStyle?.unicodePattern,
                        fractionalSecondsUnicodePattern(timeOptions.fractionalSecondDigits),
                        timeOptions.timezoneStyle?.unicodePattern,
                    )

                    null -> emptyList()
                }

                val skeleton = (dateSkeleton + timeSkeleton).joinToString(separator = " ")
                getDateFormatForSkeleton(skeleton, locale)
            }
        }
    }

    actual override fun localize(value: Zoned<Instant>): String {
        val (instant, zone) = value
        val zonedDateTime = instant.toJavaInstant().atZone(zone.toJavaZoneId())

        return dateFormat.format(zonedDateTime)
    }
}