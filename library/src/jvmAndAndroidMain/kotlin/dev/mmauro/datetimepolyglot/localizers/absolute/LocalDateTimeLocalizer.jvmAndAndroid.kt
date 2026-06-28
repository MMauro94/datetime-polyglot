package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime

actual class LocalDateTimeLocalizer actual constructor(
    options: LocalDateTimeOptions,
    locale: PlatformLocale
) : PolyglotDateTimeLocalizer<LocalDateTime> {

    private val dateFormat = ComponentsOptions(
        dateOptions = options.dateOptions.toComponentOptions(),
        timeOptions = options.timeOptions.toComponentOptions(),
    ).toDateFormat(locale)

    actual override fun localize(value: LocalDateTime): String {
        return dateFormat.format(value.toJavaLocalDateTime())
    }
}

/**
 * Allows creation of mixed style and components between date and time, which is not possible in all platforms
 *
 * @see LocalDateTimeOptions
 */
operator fun LocalDateTimeOptions.Companion.invoke(
    dateOptions: DateStyleOptions,
    timeOptions: LocalTimeOptions
): LocalDateTimeOptions = LocalDateTimeOptions(DateOptions(dateOptions), timeOptions)

/**
 * Allows creation of mixed style and components between date and time, which is not possible in all platforms
 *
 * @see LocalDateTimeOptions
 */
operator fun LocalDateTimeOptions.Companion.invoke(
    dateOptions: DateStyleOptions,
    timeOptions: TimeStyleOptions.Local
): LocalDateTimeOptions = LocalDateTimeOptions(DateOptions(dateOptions), TimeOptions(timeOptions))
