package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a calendar month.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-month](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-month)
 */
public enum class MonthStyle {

    /**
     * Not padded number, e.g. `3`
     */
    NUMERIC,

    /**
     * 2-digits zero-padded number, e.g. `03`
     */
    NUMERIC_PADDED_2_DIGITS,

    /**
     * Single letter, e.g. `D`
     */
    NARROW,

    /**
     * Usually three letters, e.g. `Dec`
     */
    ABBREVIATED,

    /**
     * Full name, e.g. `December`
     */
    WIDE,
}
