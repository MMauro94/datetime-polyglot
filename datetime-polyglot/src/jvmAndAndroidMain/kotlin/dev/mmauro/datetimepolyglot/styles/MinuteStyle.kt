package dev.mmauro.datetimepolyglot.styles

internal val MinuteStyle.unicodePattern: String
    get() = when (this) {
        MinuteStyle.NUMERIC -> "m"
        MinuteStyle.NUMERIC_PADDED_2_DIGITS -> "mm"
    }
