package dev.mmauro.datetimepolyglot.styles

internal val DayOfWeekStyle.standaloneUnicodePattern: String
    get() = when (this) {
        DayOfWeekStyle.NARROW -> "ccccc"
        DayOfWeekStyle.SHORT -> "cccccc"
        DayOfWeekStyle.ABBREVIATED -> "ccc"
        DayOfWeekStyle.WIDE -> "cccc"
    }

internal val DayOfWeekStyle.unicodePattern: String
    get() = when (this) {
        DayOfWeekStyle.NARROW -> "eeeee"
        DayOfWeekStyle.SHORT -> "eeeeee"
        DayOfWeekStyle.ABBREVIATED -> "eee"
        DayOfWeekStyle.WIDE -> "eeee"
    }