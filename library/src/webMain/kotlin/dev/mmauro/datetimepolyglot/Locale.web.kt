package dev.mmauro.datetimepolyglot

import dev.mmauro.datetimepolyglot.utils.dayOfWeekFromIndex
import js.intl.Locale
import js.intl.NumberFormat
import kotlinx.datetime.DayOfWeek
import weekstart.getWeekStartByLocale
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

internal actual typealias PlatformLocale = Locale

internal actual fun getDefaultLocale(): Locale = localeFromBcp47LanguageTag(NumberFormat().resolvedOptions().locale)

internal actual val PlatformLocale.bcp47LanguageTag: String
    get() = this.toString()

internal actual val PlatformLocale.baseLocale: PlatformLocale
    get() = localeFromBcp47LanguageTag(this.baseName)

@OptIn(ExperimentalWasmJsInterop::class)
private val hasWeekInfo: Boolean = js(""" "getWeekInfo" in Intl.Locale.prototype """)

internal actual val PlatformLocale.firstDayOfWeek: DayOfWeek
    get() {
        return if (hasWeekInfo) {
            DayOfWeek.entries[getWeekInfo().firstDay - 1]
        } else {
            extractFw() ?: dayOfWeekFromIndex(getWeekStartByLocale(this.toString()), mondayIndex = 1)
        }
    }

private val FW_MATCHER = Regex("u-fw-([a-z]{3})")

private fun PlatformLocale.extractFw(): DayOfWeek? {
    return FW_MATCHER.find(toString())?.let { result ->
        when (result.groupValues[1]) {
            "mon" -> DayOfWeek.MONDAY
            "tue" -> DayOfWeek.TUESDAY
            "wed" -> DayOfWeek.WEDNESDAY
            "thu" -> DayOfWeek.THURSDAY
            "fri" -> DayOfWeek.FRIDAY
            "sat" -> DayOfWeek.SATURDAY
            "sun" -> DayOfWeek.SUNDAY
            else -> null
        }
    }
}
