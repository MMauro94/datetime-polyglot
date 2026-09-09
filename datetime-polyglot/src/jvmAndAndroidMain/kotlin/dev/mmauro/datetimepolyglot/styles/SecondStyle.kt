package dev.mmauro.datetimepolyglot.styles

internal val SecondStyle.unicodePattern
    get() = when (this) {
        SecondStyle.NUMERIC -> "s"
        SecondStyle.NUMERIC_PADDED_2_DIGITS -> "ss"
    }

internal fun fractionalSecondsUnicodePattern(value: Int) = "S".repeat(value)
