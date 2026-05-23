package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a calendar day of a calendar month.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-day](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-day)
 */
enum class DayOfMonthStyle {

    /**
     * Not padded number, e.g. `9`
     */
    NUMERIC,

    /**
     * 2-digits zero-padded number, e.g. `09`
     */
    NUMERIC_PADDED_2_DIGITS,
}