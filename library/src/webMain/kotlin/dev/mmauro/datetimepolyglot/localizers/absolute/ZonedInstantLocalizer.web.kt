package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.toJsInstant
import js.intl.DateTimeFormat
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.time.Instant
import js.temporal.Instant as JsInstant

actual class ZonedInstantLocalizer actual constructor(
    private val options: ZonedInstantOptions,
    private val locale: PlatformLocale
) : PolyglotDateTimeZonedLocalizer<Instant> {

    @OptIn(ExperimentalWasmJsInterop::class)
    actual override fun localize(value: Zoned<Instant>): String {
        val baseDateTimeLocalizer = BaseDateTimeLocalizer<JsInstant>(
            dateOptions = options.dateOptions,
            timeOptions = options.timeOptions,
            timeZone = value.timeZone,
            locale = locale,
            formatToParts = DateTimeFormat::formatToParts,

            // JS doesn't natively support using the time zone ID directly in the localized string,
            // We can force it by formatting to parts and overriding the specific part in this case
            partMapper = {
                if (it.type == "timeZoneName"
                    && options.timeOptions.styleOptions is ZonedTimeComponents
                    && options.timeOptions.styleOptions.timeZoneStyle == TimeZoneStyle.Generic.ID
                ) {
                    value.timeZone.id
                } else {
                    it.value
                }
            }
        )
        return baseDateTimeLocalizer.localize(value.value.toJsInstant())
    }
}