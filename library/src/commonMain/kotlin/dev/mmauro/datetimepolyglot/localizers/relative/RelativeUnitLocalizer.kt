package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle

internal expect class RelativeUnitLocalizer(style: RelativeUnitStyle, locale: PlatformLocale) {
    fun localizeNumeric(value: Double, unit: RelativeUnit.DateTimeComponent): String
    fun localizeDiffDirection(direction: RelativeDirection, unit: RelativeUnit): String?
    fun localizeNow(): String?
}

internal sealed interface RelativeUnit {

    data class DayOfWeek(val dayOfWeek: kotlinx.datetime.DayOfWeek) : RelativeUnit

    enum class DateTimeComponent : RelativeUnit {
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR,
    }
}

public enum class RelativeDirection(public val offset: Int) {
    LAST_2(-2),
    LAST(-1),
    THIS(0),
    NEXT(1),
    NEXT_2(2),
}

internal fun RelativeUnitLocalizer.localizeDiffDirection(value: Double, unit: RelativeUnit.DateTimeComponent, allowedDirection: Set<RelativeDirection>): String? {
    val direction = RelativeDirection.entries.find { it.offset == value.toInt() }
    if (direction != null && direction in allowedDirection) {
        localizeDiffDirection(direction, unit)?.let { return it }
    }
    return null
}

internal interface RelativeUnitOptions {
    val style: RelativeUnitStyle
    val allowedDirections: Set<RelativeDirection>
}