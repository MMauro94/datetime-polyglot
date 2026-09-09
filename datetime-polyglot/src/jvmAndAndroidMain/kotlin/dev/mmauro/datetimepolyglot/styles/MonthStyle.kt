package dev.mmauro.datetimepolyglot.styles

internal val MonthStyle.standaloneUnicodePattern: String
    get() = when (this) {
        MonthStyle.NUMERIC -> "L"
        MonthStyle.NUMERIC_PADDED_2_DIGITS -> "LL"
        MonthStyle.NARROW -> "LLLLL"
        MonthStyle.ABBREVIATED -> "LLL"
        MonthStyle.WIDE -> "LLLL"
    }

internal val MonthStyle.unicodePattern: String
    get() = when (this) {
        MonthStyle.NUMERIC -> "M"
        MonthStyle.NUMERIC_PADDED_2_DIGITS -> "MM"
        MonthStyle.NARROW -> "MMMMM"
        MonthStyle.ABBREVIATED -> "MMM"
        MonthStyle.WIDE -> "MMMM"
    }
