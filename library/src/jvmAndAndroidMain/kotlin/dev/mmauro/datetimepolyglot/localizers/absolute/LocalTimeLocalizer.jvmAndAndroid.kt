package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime

public actual class LocalTimeLocalizer actual constructor(
    options: LocalTimeOptions<*>,
    locale: PlatformLocale
) : PolyglotDateTimeLocalizer<LocalTime> {

    public actual constructor(options: LocalTimeStyleOptions, locale: PlatformLocale) : this(LocalTimeOptions(options), locale)

    private val dateFormat = ComponentsOptions(timeOptions = options.toComponentOptions()).toDateFormat(locale)

    actual override fun localize(value: LocalTime): String {
        return dateFormat.format(value.toJavaLocalTime())
    }
}