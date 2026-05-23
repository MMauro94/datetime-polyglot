package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a time zone.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone)
 */
enum class TimeZoneStyle {

    /**
     * e.g. `PDT`
     */
    SPECIFIC_NON_LOCATION_SHORT,

    /**
     * e.g. `Pacific Daylight Time`
     */
    SPECIFIC_NON_LOCATION_LONG,

    /**
     * e.g. `PT`
     */
    GENERIC_NON_LOCATION_SHORT,

    /**
     * e.g. `Pacific Time`
     */
    GENERIC_NON_LOCATION_LONG,

    /**
     * e.g. `GMT-8`
     */
    GMT_SHORT,

    /**
     * e.g. `GMT-08:00`
     */
    GMT_LONG,

    // TODO add JS unsupported ones
}