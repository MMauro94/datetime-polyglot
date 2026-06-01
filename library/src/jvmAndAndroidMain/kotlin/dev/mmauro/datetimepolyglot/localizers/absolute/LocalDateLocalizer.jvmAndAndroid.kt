package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

actual class LocalDateLocalizer actual constructor(
    options: DateOptions,
    locale: PlatformLocale
) : DateTimeLocalizer<LocalDate> {

    private val dateFormat = ComponentsOptions(dateOptions = options.toComponentOptions()).toDateFormat(locale)

    actual override fun localize(value: LocalDate): String {
        return dateFormat.format(value.toJavaLocalDate())
    }
}