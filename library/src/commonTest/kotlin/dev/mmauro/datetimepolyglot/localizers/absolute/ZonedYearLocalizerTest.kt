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
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.datetime.TimeZone

val ZonedYearLocalizerTestFactory = funSpec {
    test("basic test") {
        val localizer = ZonedYearLocalizer(locale = LOCALE_ENGLISH)
        localizer.localize(Zoned(2026, TimeZone.of("America/Los_Angeles"))) shouldBeLocalizedAs when (TEST_PLATFORM) {
            // Web platforms don't support default LOCATION time zone style
            is TestPlatform.Js, is TestPlatform.Wasm -> "2026, Pacific Time"
            else -> "2026, Los Angeles Time"
        }
    }

    test("with options") {
        ZonedYearLocalizer(
            options = ZonedYearOptions(
                yearOptions = YearOptions(eraStyle = EraStyle.ABBREVIATED),
                timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.NON_LOCATION_SHORT)
            ),
            locale = LOCALE_ENGLISH,
        ).localize(Zoned(2026, TimeZone.of("Australia/Sydney"))) shouldBeLocalizedAs "2026 AD, Sydney Time"
    }

    test("in another language") {
        ZonedYearLocalizer(
            options = ZonedYearOptions(
                yearOptions = YearOptions(yearStyle = YearStyle.NUMERIC_2_DIGITS),
                timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.NON_LOCATION_LONG)
            ),
            locale = LOCALE_ITALIAN,
        ).localize(Zoned(1999, TimeZone.of("Europe/Rome"))) shouldBeLocalizedAs "99, Ora dell’Europa centrale"
    }
}

class ZonedYearLocalizerTest : FunSpec({
    include(ZonedYearLocalizerTestFactory)
})