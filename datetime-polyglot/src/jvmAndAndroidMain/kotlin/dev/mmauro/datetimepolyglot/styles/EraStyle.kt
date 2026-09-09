package dev.mmauro.datetimepolyglot.styles

internal val EraStyle.unicodePattern: String
    get() = when (this) {
        EraStyle.NARROW -> "GGGGG"
        EraStyle.ABBREVIATED -> "GGG"
        EraStyle.WIDE -> "GGGG"
    }
