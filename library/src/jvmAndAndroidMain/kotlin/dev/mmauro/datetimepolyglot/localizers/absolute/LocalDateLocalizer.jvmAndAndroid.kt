package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

actual class LocalDateLocalizer internal actual constructor(
    options: DateOptions,
    locale: PlatformLocale
) : PolyglotDateTimeLocalizer<LocalDate> {

    actual constructor(options: DateStyleOptions, locale: PlatformLocale) : this(DateOptions(options), locale)

    private val dateFormat = ComponentsOptions(dateOptions = options.toComponentOptions()).toDateFormat(locale)

    actual override fun localize(value: LocalDate): String {
        return dateFormat.format(value.toJavaLocalDate())
    }
}