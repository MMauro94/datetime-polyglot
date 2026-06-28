package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDisplayName
import kotlinx.datetime.TimeZone

actual class TimeZoneLocalizer actual constructor(
    private val options: TimeZoneOptions,
    private val locale: PlatformLocale
) : PolyglotDateTimeLocalizer<TimeZone> {

    actual override fun localize(value: TimeZone): String {
        return value.getDisplayName(options.style, locale)
    }
}