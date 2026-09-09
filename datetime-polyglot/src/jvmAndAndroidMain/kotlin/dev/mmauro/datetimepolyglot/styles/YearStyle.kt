package dev.mmauro.datetimepolyglot.styles

internal val YearStyle.unicodePattern: String
    get() = when (this) {
        YearStyle.NUMERIC -> "y"
        YearStyle.NUMERIC_2_DIGITS -> "yy"
        YearStyle.NUMERIC_PADDED_4_DIGITS -> "yyyy"
    }
