package dev.mmauro.datetimepolyglot

import android.icu.util.Calendar
import android.icu.util.ULocale
import dev.mmauro.datetimepolyglot.utils.dayOfWeekFromIndex
import kotlinx.datetime.DayOfWeek

internal actual val LOCALE_ENGLISH = ULocale.ENGLISH

internal actual val PlatformLocale.bcp47LanguageTag: String
    get() = toLanguageTag()

internal actual val PlatformLocale.basePlatformLocale: PlatformLocale
    get() = ULocale(this.baseName)

internal actual val PlatformLocale.firstDayOfWeek: DayOfWeek
    get() = Calendar.getInstance(this).firstDayOfWeek.let { dayOfWeekFromIndex(it, mondayIndex = 2) }
