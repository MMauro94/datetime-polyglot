package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a day of the week.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-weekday](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-weekday)
 */
enum class DayOfWeekStyle {
    /**
     * Single letter, e.g. `T`
     */
    NARROW,

    /**
     * Two letters, e.g. `Tu`
     *
     * WARNING: Might not be supported by all targets, will fall back to [ABBREVIATED] in such cases.
     */
    SHORT,

    /**
     * Three letters, e.g. `Tue`
     */
    ABBREVIATED,

    /**
     * Full name, e.g. `Tuesday`
     */
    WIDE,
}