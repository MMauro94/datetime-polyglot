package dev.mmauro.datetimepolyglot.styles

internal val DayOfMonthStyle.unicodePattern: String
    get() = when (this) {
        DayOfMonthStyle.NUMERIC -> "d"
        DayOfMonthStyle.NUMERIC_PADDED_2_DIGITS -> "dd"
    }