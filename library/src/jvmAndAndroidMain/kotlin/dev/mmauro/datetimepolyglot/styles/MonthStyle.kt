package dev.mmauro.datetimepolyglot.styles

internal val MonthStyle.unicodePattern: String
    get() = when (this) {
        MonthStyle.NUMERIC -> "M"
        MonthStyle.NUMERIC_PADDED_2_DIGITS -> "MM"
        MonthStyle.NARROW -> "MMMMM"
        MonthStyle.ABBREVIATED -> "MMM"
        MonthStyle.WIDE -> "MMMM"
    }