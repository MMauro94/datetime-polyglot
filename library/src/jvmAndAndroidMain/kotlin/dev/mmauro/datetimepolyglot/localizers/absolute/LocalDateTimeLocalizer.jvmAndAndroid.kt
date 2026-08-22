package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime

public actual class LocalDateTimeLocalizer actual constructor(
    options: LocalDateTimeOptions,
    locale: PlatformLocale,
) : PolyglotDateTimeLocalizer<LocalDateTime> {

    private val dateFormat = ComponentsOptions(
        dateOptions = options.dateOptions.toComponentOptions(),
        timeOptions = options.timeOptions.toComponentOptions(),
    ).toDateFormat(locale)

    actual override fun localize(value: LocalDateTime): String {
        return dateFormat.format(value.toJavaLocalDateTime())
    }
}
