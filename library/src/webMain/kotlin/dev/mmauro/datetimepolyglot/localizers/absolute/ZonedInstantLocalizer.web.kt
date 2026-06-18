package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.toJsInstant
import js.array.asSequence
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.intl.TimeZoneNameFormat
import js.intl.long
import js.intl.shortOffset
import js.objects.unsafeJso
import kotlin.js.toList
import kotlin.time.Instant

actual class ZonedInstantLocalizer actual constructor(
    private val options: ZonedInstantOptions,
    private val locale: PlatformLocale
) : DateTimeZonedLocalizer<Instant> {

    actual override fun localize(value: Zoned<Instant>): String {
        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            fill(options.dateOptions.toComponentOptions())
            fill(options.timeOptions.toComponentOptions(), timeZoneIdFallback = TimeZoneNameFormat.shortOffset)

            timeZone = value.timeZone.id
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)

        // JS doesn't natively support using the time zone ID directly in the localized string,
        // We can force it by formatting to parts and overriding the specific part in this case
        return format.formatToParts(value.value.toJsInstant()).asSequence().joinToString(separator = "") {
            if (it.type == "timeZoneName"
                && options.timeOptions.styleOptions is TimeComponents.Zoned
                && options.timeOptions.styleOptions.timeZoneStyle == TimeZoneStyle.Generic.ID
            ) {
                value.timeZone.id
            } else {
                it.value
            }
        }
    }
}