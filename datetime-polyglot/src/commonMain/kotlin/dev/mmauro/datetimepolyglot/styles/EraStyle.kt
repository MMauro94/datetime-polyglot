package dev.mmauro.datetimepolyglot.styles

/**
 * Style of an era.
 *
 * See [https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-era](https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-era)
 */
public enum class EraStyle {

    /**
     * e.g. `A`
     */
    NARROW,

    /**
     * e.g. `AD`
     */
    ABBREVIATED,

    /**
     * Full name, e.g. `Anno Domini`
     */
    WIDE,
}
