package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toPlainTime
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.LocalTime

actual class LocalTimeLocalizer actual constructor(
    private val options: TimeOptions<TimeStyleOptions.Local>,
    private val locale: PlatformLocale
) : DateTimeLocalizer<LocalTime> {
    actual override fun localize(value: LocalTime): String {
        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            fill(options.toComponentOptions())
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(value.toPlainTime())
    }
}