package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.toJavaZoneId
import kotlin.time.Instant
import kotlin.time.toJavaInstant

public actual class ZonedInstantLocalizer actual constructor(
    options: ZonedInstantOptions,
    locale: PlatformLocale
) : PolyglotDateTimeZonedLocalizer<Instant> {

    private val dateFormat = ComponentsOptions(
        dateOptions = options.dateOptions.toComponentOptions(),
        timeOptions = options.timeOptions.toComponentOptions(),
    ).toDateFormat(locale)

    actual override fun localize(value: Zoned<Instant>): String {
        return dateFormat.format(value.value.toJavaInstant().atZone(value.timeZone.toJavaZoneId()))
    }
}
