package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale

internal expect class RelativeUnitLocalizer(style: RelativeUnitStyle, locale: PlatformLocale) {
    fun localizeNumeric(value: Double, unit: RelativeUnit): String
    fun localizeDirection(direction: RelativeDirection, unit: RelativeUnit): String?
    fun localizeNow(): String?
}

internal enum class RelativeUnit {
    SECOND,
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR,
}

internal enum class RelativeUnitStyle {
    NARROW,
    SHORT,
    LONG,
}

internal enum class RelativeDirection(val offset: Int) {
    LAST_2(-2),
    LAST(-1),
    THIS(0),
    NEXT(1),
    NEXT_2(2),
}