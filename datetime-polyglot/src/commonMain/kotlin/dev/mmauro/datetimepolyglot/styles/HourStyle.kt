package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a hour of day.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-hour](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-hour)
 */
public enum class HourStyle {

    /**
     * Not padded number, e.g. `1`
     */
    NUMERIC,

    /**
     * 2-digits zero-padded number, e.g. `01`
     */
    NUMERIC_PADDED_2_DIGITS,
}
