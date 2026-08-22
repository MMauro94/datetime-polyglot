package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.utils.localizeRelativeDayOfWeek
import js.intl.RelativeTimeFormat
import js.intl.RelativeTimeFormatNumeric
import js.intl.RelativeTimeFormatStyle
import js.intl.RelativeTimeFormatUnit
import js.intl.always
import js.intl.auto
import js.intl.day
import js.intl.hour
import js.intl.long
import js.intl.minute
import js.intl.month
import js.intl.narrow
import js.intl.second
import js.intl.short
import js.intl.week
import js.intl.year
import js.objects.unsafeJso

internal actual class RelativeUnitLocalizer actual constructor(
    private val style: RelativeUnitStyle,
    private val locale: PlatformLocale,
) {

    private val numericRelativeTimeFormat = RelativeTimeFormat(
        locale,
        unsafeJso {
            this.style = this@RelativeUnitLocalizer.style.toJsRelativeTimeFormatStyle()
            this.numeric = RelativeTimeFormatNumeric.always
        },
    )

    private val relativeTimeFormat = RelativeTimeFormat(
        locale,
        unsafeJso {
            this.style = this@RelativeUnitLocalizer.style.toJsRelativeTimeFormatStyle()
            this.numeric = RelativeTimeFormatNumeric.auto
        },
    )

    actual fun localizeNumeric(value: Double, unit: RelativeUnit.DateTimeComponent): String {
        return numericRelativeTimeFormat.format(value, unit.toJsRelativeTimeFormatUnit())
    }

    actual fun localizeDiffDirection(direction: RelativeDirection, unit: RelativeUnit): String? {
        // JS returns the string "now" for this case
        if (direction == RelativeDirection.THIS && unit == RelativeUnit.DateTimeComponent.SECOND) {
            return null
        }

        return when (unit) {
            is RelativeUnit.DateTimeComponent -> {
                val formatted = relativeTimeFormat.format(direction.offset.toDouble(), unit.toJsRelativeTimeFormatUnit())

                // We need to return null if the direction format doesn't exist
                // Since JS doesn't expose this directly, we are just checking if the format is identical to the numeric one
                if (formatted == localizeNumeric(direction.offset.toDouble(), unit)) {
                    null
                } else {
                    formatted
                }
            }
            is RelativeUnit.DayOfWeek -> localizeRelativeDayOfWeek(locale, style, direction, unit.dayOfWeek)
        }
    }

    actual fun localizeNow(): String? {
        return relativeTimeFormat.format(0.0, RelativeTimeFormatUnit.second)
    }
}

private fun RelativeUnitStyle.toJsRelativeTimeFormatStyle(): RelativeTimeFormatStyle {
    return when (this) {
        RelativeUnitStyle.NARROW -> RelativeTimeFormatStyle.narrow
        RelativeUnitStyle.SHORT -> RelativeTimeFormatStyle.short
        RelativeUnitStyle.LONG -> RelativeTimeFormatStyle.long
    }
}

private fun RelativeUnit.DateTimeComponent.toJsRelativeTimeFormatUnit(): RelativeTimeFormatUnit {
    return when (this) {
        RelativeUnit.DateTimeComponent.SECOND -> RelativeTimeFormatUnit.second
        RelativeUnit.DateTimeComponent.MINUTE -> RelativeTimeFormatUnit.minute
        RelativeUnit.DateTimeComponent.HOUR -> RelativeTimeFormatUnit.hour
        RelativeUnit.DateTimeComponent.DAY -> RelativeTimeFormatUnit.day
        RelativeUnit.DateTimeComponent.WEEK -> RelativeTimeFormatUnit.week
        RelativeUnit.DateTimeComponent.MONTH -> RelativeTimeFormatUnit.month
        RelativeUnit.DateTimeComponent.YEAR -> RelativeTimeFormatUnit.year
    }
}
