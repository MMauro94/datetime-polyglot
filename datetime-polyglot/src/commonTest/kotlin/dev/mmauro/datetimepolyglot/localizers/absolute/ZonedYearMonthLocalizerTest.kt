@file:OptIn(ExperimentalZonedLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.ExperimentalZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth

val ZonedYearMonthLocalizerTestFactory = funSpec {
    test("basic test") {
        val localizer = ZonedYearMonthLocalizer(
            options = ZonedYearMonthOptions(YearMonthOptions(monthStyle = MonthStyle.ABBREVIATED)),
            locale = LOCALE_ENGLISH,
        )
        val zonedYearMonth = Zoned(YearMonth(2026, Month.JULY), TimeZone.of("America/Los_Angeles"))
        localizer.localize(zonedYearMonth) shouldBeLocalizedAs when (TEST_PLATFORM) {
            // Web platforms don't support default LOCATION time zone style
            is TestPlatform.Js, is TestPlatform.Wasm -> "Jul 2026, Pacific Time"
            else -> "Jul 2026, Los Angeles Time"
        }
    }

    test("with options") {
        val zonedYearMonth = Zoned(YearMonth(2026, Month.JULY), TimeZone.of("Australia/Sydney"))
        ZonedYearMonthLocalizer(
            options = ZonedYearMonthOptions(
                yearMonthOptions = YearMonthOptions(
                    eraStyle = EraStyle.ABBREVIATED,
                    monthStyle = MonthStyle.WIDE,
                ),
                timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.NON_LOCATION_SHORT),
            ),
            locale = LOCALE_ENGLISH,
        ).localize(zonedYearMonth) shouldBeLocalizedAs "July 2026 AD, Sydney Time"
    }

    test("in another language") {
        val zonedYearMonth = Zoned(YearMonth(1999, Month.APRIL), TimeZone.of("Europe/Rome"))
        ZonedYearMonthLocalizer(
            options = ZonedYearMonthOptions(
                yearMonthOptions = YearMonthOptions(
                    eraStyle = EraStyle.ABBREVIATED,
                    yearStyle = YearStyle.NUMERIC_2_DIGITS,
                    monthStyle = MonthStyle.WIDE,
                ),
                timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.NON_LOCATION_LONG),
            ),
            locale = LOCALE_ITALIAN,
        ).localize(zonedYearMonth) shouldBeLocalizedAs "aprile 99 d.C., Ora dell’Europa centrale"
    }
}

class ZonedYearMonthLocalizerTest : FunSpec({
    include(ZonedYearMonthLocalizerTestFactory)
})
