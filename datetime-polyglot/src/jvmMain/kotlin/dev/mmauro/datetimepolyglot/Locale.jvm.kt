package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import dev.mmauro.datetimepolyglot.utils.dayOfWeekFromIndex
import kotlinx.datetime.DayOfWeek

internal actual val LOCALE_ENGLISH = PlatformLocale.ENGLISH

internal fun PlatformLocale.toULocale() = ULocale.forLanguageTag(toLanguageTag())

internal actual val PlatformLocale.bcp47LanguageTag: String
    get() = toLanguageTag()

internal actual val PlatformLocale.basePlatformLocale: PlatformLocale
    get() = ULocale(this.toULocale().baseName).toLocale()

internal actual val PlatformLocale.firstDayOfWeek: DayOfWeek
    get() = Calendar.getInstance(this).firstDayOfWeek.let { dayOfWeekFromIndex(it, mondayIndex = 2) }
