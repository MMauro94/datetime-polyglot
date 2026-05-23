package dev.mmauro.datetimepolyglot.styles

internal val TimeZoneStyle.unicodePattern: String
    get() = when (this) {
        TimeZoneStyle.SPECIFIC_NON_LOCATION_SHORT -> "zzz"
        TimeZoneStyle.SPECIFIC_NON_LOCATION_LONG -> "zzzz"
        TimeZoneStyle.GENERIC_NON_LOCATION_SHORT -> "v"
        TimeZoneStyle.GENERIC_NON_LOCATION_LONG -> "vvvv"
        TimeZoneStyle.GMT_SHORT -> "O"
        TimeZoneStyle.GMT_LONG -> "OOOO"
    }