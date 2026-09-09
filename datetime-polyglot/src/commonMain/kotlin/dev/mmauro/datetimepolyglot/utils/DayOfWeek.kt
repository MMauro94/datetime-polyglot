package dev.mmauro.datetimepolyglot.utils

import kotlinx.datetime.DayOfWeek

internal fun dayOfWeekFromIndex(index: Int, mondayIndex: Int): DayOfWeek {
    return DayOfWeek.entries.single {
        it.ordinal == (index - mondayIndex).mod(DayOfWeek.entries.size)
    }
}
