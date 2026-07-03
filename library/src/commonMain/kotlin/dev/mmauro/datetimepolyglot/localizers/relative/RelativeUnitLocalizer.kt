package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle

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

enum class RelativeDirection(val offset: Int) {
    LAST_2(-2),
    LAST(-1),
    THIS(0),
    NEXT(1),
    NEXT_2(2),
}

internal fun RelativeUnitLocalizer.localize(value: Double, unit: RelativeUnit, allowedDirection: List<RelativeDirection>): String {
    val direction = RelativeDirection.entries.find { it.offset == value.toInt() }
    if (direction != null && direction in allowedDirection) {
        localizeDirection(direction, unit)?.let { return it }
    }
    return localizeNumeric(value, unit)
}

internal interface RelativeUnitOptions {
    val style: RelativeUnitStyle
    val allowedDirections: List<RelativeDirection>
}