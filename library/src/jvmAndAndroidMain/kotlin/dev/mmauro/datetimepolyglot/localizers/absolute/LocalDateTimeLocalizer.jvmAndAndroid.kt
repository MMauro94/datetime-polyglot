package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime

actual class LocalDateTimeLocalizer actual constructor(
    options: DateTimeOptions,
    locale: PlatformLocale
) : DateTimeLocalizer<LocalDateTime> {

    private val dateFormat = ComponentsOptions(
        dateOptions = options.dateOptions.toComponentOptions(),
        timeOptions = options.timeOptions.toComponentOptions(),
    ).toDateFormat(locale)

    actual override fun localize(value: LocalDateTime): String {
        return dateFormat.format(value.toJavaLocalDateTime())
    }
}

operator fun DateTimeOptions.Companion.invoke(
    dateOptions: DateStyleOptions,
    timeOptions: LocalTimeOptions
): DateTimeOptions = DateTimeOptions(DateOptions(dateOptions), timeOptions)

operator fun DateTimeOptions.Companion.invoke(
    dateOptions: DateStyleOptions,
    timeOptions: TimeStyleOptions.Local
): DateTimeOptions = DateTimeOptions(DateOptions(dateOptions), TimeOptions(timeOptions))
