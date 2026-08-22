package dev.mmauro.datetimepolyglot.styles

import js.intl.TimeZoneNameFormat
import js.intl.long
import js.intl.longGeneric
import js.intl.longOffset
import js.intl.short
import js.intl.shortGeneric
import js.intl.shortOffset

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#timezonename)
 */
internal fun TimeZoneStyle.toTimeZoneNameFormat(idFallback: TimeZoneNameFormat? = null): TimeZoneNameFormat = when (this) {
    // Generic
    TimeZoneStyle.Generic.ID -> idFallback ?: error("Unsupported TimeZoneNameFormat ID: should be handled separately")
    TimeZoneStyle.Generic.NON_LOCATION_SHORT -> TimeZoneNameFormat.shortGeneric
    TimeZoneStyle.Generic.NON_LOCATION_LONG -> TimeZoneNameFormat.longGeneric
    // Unsupported, falls back to NON_LOCATION_LONG
    TimeZoneStyle.Generic.LOCATION -> TimeZoneNameFormat.longGeneric
    // Specific
    TimeZoneStyle.Specific.NON_LOCATION_SHORT -> TimeZoneNameFormat.short
    TimeZoneStyle.Specific.NON_LOCATION_LONG -> TimeZoneNameFormat.long
    // GMT
    TimeZoneStyle.Gmt.SHORT -> TimeZoneNameFormat.shortOffset
    TimeZoneStyle.Gmt.LONG -> TimeZoneNameFormat.longOffset
}
