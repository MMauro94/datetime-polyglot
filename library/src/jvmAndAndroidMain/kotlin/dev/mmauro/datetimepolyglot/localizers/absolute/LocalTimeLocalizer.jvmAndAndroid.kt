package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime

actual class LocalTimeLocalizer actual constructor(
    options: TimeOptions<TimeStyleOptions.Local>,
    locale: PlatformLocale
) : DateTimeLocalizer<LocalTime> {

    private val dateFormat = ComponentsOptions(timeOptions = options.toComponentOptions()).toDateFormat(locale)

    actual override fun localize(value: LocalTime): String {
        return dateFormat.format(value.toJavaLocalTime())
    }
}