package dev.mmauro.datetimepolyglot

internal val HourCycle.unicodePatternChar: Char
    get() = when (this) {
        HourCycle.HOURS_11 -> 'K'
        HourCycle.HOURS_12 -> 'h'
        HourCycle.HOURS_23 -> 'H'
        HourCycle.HOURS_24 -> 'k'
    }
