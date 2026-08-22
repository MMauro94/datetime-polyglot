package dev.mmauro.datetimepolyglot.styles

import kotlin.time.Duration

/**
 * Style of a [Duration].
 *
 * Note that while in English this primarily affects the style of the units (e.g. `min` vs `minutes`), in other languages this also affects
 * the style of the list (other languages might have formats for list widths).
 */
public enum class DurationStyle {

    /**
     * e.g. `1h 5m`
     */
    NARROW,

    /**
     * e.g. `1 hr, 5 min`
     */
    SHORT,

    /**
     * e.g. `1 hour, 5 minutes`
     */
    WIDE,
}