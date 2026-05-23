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
internal fun TimeZoneStyle.toTimeZoneNameFormat(): TimeZoneNameFormat = when (this) {
    TimeZoneStyle.SPECIFIC_NON_LOCATION_SHORT -> TimeZoneNameFormat.short
    TimeZoneStyle.SPECIFIC_NON_LOCATION_LONG -> TimeZoneNameFormat.long
    TimeZoneStyle.GENERIC_NON_LOCATION_SHORT -> TimeZoneNameFormat.shortGeneric
    TimeZoneStyle.GENERIC_NON_LOCATION_LONG -> TimeZoneNameFormat.longGeneric
    TimeZoneStyle.GMT_SHORT -> TimeZoneNameFormat.shortOffset
    TimeZoneStyle.GMT_LONG -> TimeZoneNameFormat.longOffset
}