package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a minute of hour.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-minute](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-minute)
 */
public enum class MinuteStyle {

    /**
     * Not padded number, e.g. `7`
     */
    NUMERIC,

    /**
     * 2-digits zero-padded number, e.g. `07`
     */
    NUMERIC_PADDED_2_DIGITS,
}