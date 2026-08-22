package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a calendar year.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-year](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-year)
 */
public enum class YearStyle {

    /**
     * Numeric year, no padding, e.g. `123` or `2026`
     */
    NUMERIC,

    /**
     * Last two digits of the year, e.g. `99` or `06`
     */
    NUMERIC_2_DIGITS,

    /**
     * 4-digits zero-padded number, e.g. `0123` or `12345`
     *
     * WARNING: Might not be supported by all targets, will fall back to [NUMERIC] in such cases.
     */
    NUMERIC_PADDED_4_DIGITS,
}