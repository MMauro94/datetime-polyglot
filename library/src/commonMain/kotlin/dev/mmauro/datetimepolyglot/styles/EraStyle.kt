package dev.mmauro.datetimepolyglot.styles

/**
 * Style of an era.
 *
 * See [hhttps://www.unicode.org/reports/tr35/tr35-dates.html#dfst-era](hhttps://www.unicode.org/reports/tr35/tr35-dates.html#dfst-era)
 */
enum class EraStyle {

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