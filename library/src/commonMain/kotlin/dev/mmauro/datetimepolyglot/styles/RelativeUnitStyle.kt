package dev.mmauro.datetimepolyglot.styles

/**
 * Style of a relative unit
 */
enum class RelativeUnitStyle {
    /**
     * e.g. `h` for hours
     */
    NARROW,

    /**
     * `hrs.` for hours
     */
    SHORT,

    /**
     * `hours` for hours
     */
    LONG,
}