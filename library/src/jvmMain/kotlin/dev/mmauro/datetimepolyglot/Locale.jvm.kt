package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import dev.mmauro.datetimepolyglot.utils.dayOfWeekFromIndex
import kotlinx.datetime.DayOfWeek

internal actual val LOCALE_ENGLISH = PlatformLocale.ENGLISH

internal actual typealias PlatformLocale = ULocale

internal actual fun getDefaultLocale() = ULocale.getDefault()

internal actual val PlatformLocale.bcp47LanguageTag: String
    get() = toLanguageTag()

internal actual fun localeFromBcp47LanguageTag(tag: String): PlatformLocale {
    return ULocale.forLanguageTag(tag)
}

internal actual val PlatformLocale.baseLocale: PlatformLocale
    get() = ULocale(this.baseName)

internal actual val PlatformLocale.firstDayOfWeek: DayOfWeek
    get() = Calendar.getInstance(this).firstDayOfWeek.let { dayOfWeekFromIndex(it, mondayIndex = 2) }