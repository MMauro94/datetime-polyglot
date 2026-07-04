package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.formatDirection
import dev.mmauro.datetimepolyglot.formatNow
import dev.mmauro.datetimepolyglot.formatNumeric
import dev.mmauro.datetimepolyglot.getRelativeDateTimeFormatter
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle

internal actual class RelativeUnitLocalizer actual constructor(
    style: RelativeUnitStyle,
    locale: PlatformLocale,
) {
    private val relativeDateTimeFormatter = getRelativeDateTimeFormatter(locale, style)

    actual fun localizeNumeric(value: Double, unit: RelativeUnit.DateTimeComponent): String {
        return relativeDateTimeFormatter.formatNumeric(value, unit)
    }

    actual fun localizeDirection(direction: RelativeDirection, unit: RelativeUnit): String? {
        return relativeDateTimeFormatter.formatDirection(direction, unit)
    }

    actual fun localizeNow(): String? {
        return relativeDateTimeFormatter.formatNow()
    }
}