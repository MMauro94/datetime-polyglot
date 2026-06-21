package dev.mmauro.datetimepolyglot.styles

internal val TimeZoneStyle.unicodePattern: String
    get() = when (this) {
        // Generic
        TimeZoneStyle.Generic.ID -> "VV"
        TimeZoneStyle.Generic.NON_LOCATION_SHORT -> "v"
        TimeZoneStyle.Generic.NON_LOCATION_LONG -> "vvvv"
        TimeZoneStyle.Generic.LOCATION -> "VVVV"

        // Specific
        TimeZoneStyle.Specific.NON_LOCATION_SHORT -> "zzz"
        TimeZoneStyle.Specific.NON_LOCATION_LONG -> "zzzz"

        // GMT
        TimeZoneStyle.Gmt.SHORT -> "O"
        TimeZoneStyle.Gmt.LONG -> "OOOO"
    }