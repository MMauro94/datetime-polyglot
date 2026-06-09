package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toPlainDateTime
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.LocalDateTime

actual class LocalDateTimeLocalizer actual constructor(
    private val options: DateTimeOptions,
    private val locale: PlatformLocale
) : DateTimeLocalizer<LocalDateTime> {

    actual override fun localize(value: LocalDateTime): String {
        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            fill(options.dateOptions.toComponentOptions())
            fill(options.timeOptions.toComponentOptions())
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(value.toPlainDateTime())
    }
}