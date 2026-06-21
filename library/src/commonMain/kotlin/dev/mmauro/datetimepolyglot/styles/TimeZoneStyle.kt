package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a time zone.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone)
 */
sealed interface TimeZoneStyle {

    enum class Generic : TimeZoneStyle {
        /**
         * e.g. `America/Los_Angeles`
         */
        ID,

        /**
         * e.g. `PT`
         */
        NON_LOCATION_SHORT,

        /**
         * e.g. `Pacific Time`
         */
        NON_LOCATION_LONG,

        /**
         * e.g. `Los Angeles Time`
         *
         * Note: not available in all platforms, falls back to [NON_LOCATION_LONG]
         */
        LOCATION,
    }

    enum class Specific : TimeZoneStyle {

        /**
         * e.g. `PDT`
         */
        NON_LOCATION_SHORT,

        /**
         * e.g. `Pacific Daylight Time`
         */
        NON_LOCATION_LONG,
    }

    enum class Gmt : TimeZoneStyle {

        /**
         * e.g. `GMT-8`
         */
        SHORT,

        /**
         * e.g. `GMT-08:00`
         */
        LONG,
    }
}