package dev.mmauro.datetimepolyglot.styles

internal val DayPeriodStyle.unicodePattern: String
    get() = when (this) {
        DayPeriodStyle.FLEXIBLE_NARROW -> "BBBBB"
        DayPeriodStyle.FLEXIBLE_ABBREVIATED -> "BBB"
        DayPeriodStyle.FLEXIBLE_WIDE -> "BBBB"
    }
