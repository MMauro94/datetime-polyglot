package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils

import com.ibm.icu.text.RelativeDateTimeFormatter
import kotlinx.datetime.DayOfWeek

fun DayOfWeek.toAbsoluteUnit(): RelativeDateTimeFormatter.AbsoluteUnit = when (this) {
    DayOfWeek.MONDAY -> RelativeDateTimeFormatter.AbsoluteUnit.MONDAY
    DayOfWeek.TUESDAY -> RelativeDateTimeFormatter.AbsoluteUnit.TUESDAY
    DayOfWeek.WEDNESDAY -> RelativeDateTimeFormatter.AbsoluteUnit.WEDNESDAY
    DayOfWeek.THURSDAY -> RelativeDateTimeFormatter.AbsoluteUnit.THURSDAY
    DayOfWeek.FRIDAY -> RelativeDateTimeFormatter.AbsoluteUnit.FRIDAY
    DayOfWeek.SATURDAY -> RelativeDateTimeFormatter.AbsoluteUnit.SATURDAY
    DayOfWeek.SUNDAY -> RelativeDateTimeFormatter.AbsoluteUnit.SUNDAY
}