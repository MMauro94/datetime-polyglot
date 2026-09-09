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
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone

val ZonedLocalDateLocalizerTestFactory = funSpec {
    test("basic test") {
        val localizer = ZonedLocalDateLocalizer(
            options = ZonedDateOptions(DateStyle.MEDIUM),
            locale = LOCALE_ENGLISH,
        )
        val zonedLocalDate = Zoned(LocalDate(2026, Month.JULY, 10), TimeZone.of("America/Los_Angeles"))
        localizer.localize(zonedLocalDate) shouldBeLocalizedAs when (TEST_PLATFORM) {
            // Web platforms don't support default LOCATION time zone style
            is TestPlatform.Js, is TestPlatform.Wasm -> "Jul 10, 2026, Pacific Time"
            else -> "Jul 10, 2026, Los Angeles Time"
        }
    }

    test("with options") {
        val zonedLocalDate = Zoned(LocalDate(2026, Month.JULY, 10), TimeZone.of("Australia/Sydney"))
        ZonedLocalDateLocalizer(
            options = ZonedDateOptions(
                dateStyleOptions = DateStyle.SHORT,
                timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.NON_LOCATION_SHORT),
            ),
            locale = LOCALE_ENGLISH,
        ).localize(zonedLocalDate) shouldBeLocalizedAs "7/10/26, Sydney Time"
    }

    test("in another language") {
        val zonedLocalDate = Zoned(LocalDate(1999, Month.APRIL, 15), TimeZone.of("Europe/Rome"))
        ZonedLocalDateLocalizer(
            options = ZonedDateOptions(
                dateStyleOptions = DateStyle.LONG,
                timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.NON_LOCATION_LONG),
            ),
            locale = LOCALE_ITALIAN,
        ).localize(zonedLocalDate) shouldBeLocalizedAs "15 aprile 1999, Ora dell’Europa centrale"
    }
}

class ZonedLocalDateLocalizerTest : FunSpec({
    include(ZonedLocalDateLocalizerTestFactory)
})
