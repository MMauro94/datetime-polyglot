package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a second of minute.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-second](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-second)
 */
enum class SecondStyle {

    /**
     * Not padded number, e.g. `8`
     */
    NUMERIC,

    /**
     * 2-digits zero-padded number, e.g. `08`
     */
    NUMERIC_PADDED_2_DIGITS,
}