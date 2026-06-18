package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.toJavaZoneId
import kotlin.time.Instant
import kotlin.time.toJavaInstant

actual class ZonedInstantLocalizer actual constructor(
    options: ZonedInstantOptions,
    locale: PlatformLocale
) : DateTimeZonedLocalizer<Instant> {

    private val dateFormat = ComponentsOptions(
        dateOptions = options.dateOptions.toComponentOptions(),
        timeOptions = options.timeOptions.toComponentOptions(),
    ).toDateFormat(locale)

    actual override fun localize(value: Zoned<Instant>): String {
        return dateFormat.format(value.value.toJavaInstant().atZone(value.timeZone.toJavaZoneId()))
    }
}

/**
 * Allows creation of mixed style and components between date and time, which is not possible in all platforms
 *
 * @see ZonedInstantOptions
 */
operator fun ZonedInstantOptions.Companion.invoke(
    dateOptions: DateStyleOptions,
    timeOptions: ZonedTimeOptions
): ZonedInstantOptions = ZonedInstantOptions(DateOptions(dateOptions), timeOptions)

/**
 * Allows creation of mixed style and components between date and time, which is not possible in all platforms
 *
 * @see ZonedInstantOptions
 */
operator fun ZonedInstantOptions.Companion.invoke(
    dateOptions: DateStyleOptions,
    timeOptions: TimeStyleOptions.Zoned
): ZonedInstantOptions = ZonedInstantOptions(DateOptions(dateOptions), TimeOptions(timeOptions))
