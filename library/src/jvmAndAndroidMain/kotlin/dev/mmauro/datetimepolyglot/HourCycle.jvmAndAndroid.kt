package dev.mmauro.datetimepolyglot

internal val HourCycle.unicodePatternChar: Char
    get() = when (this) {
        HourCycle.HOURS_11 -> 'K'
        HourCycle.HOURS_12 -> 'h'
        HourCycle.HOURS_23 -> 'H'
        HourCycle.HOURS_24 -> 'k'
    }

internal val HourCycle.unicodeExtensionKeyValue : String
    get() = when (this) {
        HourCycle.HOURS_11 -> "h11"
        HourCycle.HOURS_12 -> "h12"
        HourCycle.HOURS_23 -> "h23"
        HourCycle.HOURS_24 -> "h24"
    }