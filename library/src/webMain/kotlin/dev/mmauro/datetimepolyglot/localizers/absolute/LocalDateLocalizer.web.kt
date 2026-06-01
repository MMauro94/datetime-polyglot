package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toJsHourCycle
import dev.mmauro.datetimepolyglot.toPlainDate
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.LocalDate

actual class LocalDateLocalizer actual constructor(
    private val options: DateOptions,
    private val locale: PlatformLocale
) : DateTimeLocalizer<LocalDate> {

    actual override fun localize(value: LocalDate): String {
        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            fill(options.toComponentOptions())
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(value.toPlainDate())
    }
}