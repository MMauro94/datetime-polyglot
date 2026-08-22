package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a time zone.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone)
 */
public sealed interface TimeZoneStyle {

    /**
     * Groups "generic" [TimeZoneStyle]s, which localize the timezone in a way that is agnostic to the actual offset of the time zone in a
     * certain moment.
     */
    public enum class Generic : TimeZoneStyle {
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

    /**
     * Groups "specific" [TimeZoneStyle]s for which the localization could include information about the offset at the given moment.
     * For instance, it could include the words "Daylight savings time".
     */
    public enum class Specific : TimeZoneStyle {
        /**
         * e.g. `PDT`
         */
        NON_LOCATION_SHORT,

        /**
         * e.g. `Pacific Daylight Time`
         */
        NON_LOCATION_LONG,
    }

    /**
     * Groups the GMT [TimeZoneStyle] for localization, that print the exact offset the timezone is in at the given moment in time.
     */
    public enum class Gmt : TimeZoneStyle {

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