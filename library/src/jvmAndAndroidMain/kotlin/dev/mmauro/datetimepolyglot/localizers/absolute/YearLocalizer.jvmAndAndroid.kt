package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import java.time.Year

actual class YearLocalizer actual constructor(
    options: YearOptions,
    locale: PlatformLocale
) : PolyglotDateTimeLocalizer<Int> {

    private val dateFormat = ComponentsOptions(dateOptions = options).toDateFormat(locale)

    actual override fun localize(value: Int): String {
        return dateFormat.format(Year.of(value).atDay(1))
    }
}