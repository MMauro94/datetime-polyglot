@file:OptIn(ExperimentalZonedLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Js
import dev.mmauro.datetimepolyglot.TestPlatform.Wasm
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.DEFAULT_INSTANT_RANGE
import dev.mmauro.datetimepolyglot.localizers.ExperimentalZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.YearOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.ZonedYearOptions
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearOptions
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeZonedYearOptions
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.localizers.year
import dev.mmauro.datetimepolyglot.localizers.zoned
import dev.mmauro.datetimepolyglot.mapValue
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.tuple
import io.kotest.datatest.withTests
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days

private val NOW_DATE = LocalDateTime.parse("2026-06-01T00:00:00")
private val NOW = Zoned(NOW_DATE.toInstant(TimeZone.UTC), TimeZone.UTC)

val DynamicZonedYearLocalizerTestFactory = funSpec {
    context("localize") {
        context("dates over relative threshold are completely absolute") {
            withTests(
                nameFn = { Pair(it.b, it.c).toString() },
                tuple(
                    DynamicZonedYearLocalizer(locale = LOCALE_ENGLISH),
                    Zoned(2028, TimeZone.of("Europe/Paris")),
                    TickingValue(
                        value = when (TEST_PLATFORM) {
                            // Web platforms don't support default LOCATION time zone style
                            is Js, is Wasm -> "2028, Central European Standard Time"
                            else -> "2028, France Time"
                        },
                        nextTick = 214.days
                    ),
                ),
                tuple(
                    DynamicZonedYearLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicZonedYearOptions(
                            absoluteOptions = ZonedYearOptions(
                                yearOptions = YearOptions(eraStyle = EraStyle.ABBREVIATED),
                                timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.ID)
                            ),
                        )
                    ),
                    Zoned(2020, TimeZone.of("Europe/Rome")),
                    TickingValue("2020 AD, Europe/Rome", nextTick = null),
                ),
            ) { (localizer, zonedYear, expected) ->
                localizer.localizeAndTestNextTick(zonedYear, NOW) shouldBeLocalizedAs expected
            }
        }
    }

    context("dates within relative threshold") {
        withTests(
            nameFn = { Pair(it.b, it.c).toString() },
            tuple(
                DynamicZonedYearLocalizer(locale = LOCALE_ENGLISH),
                Zoned(2026, TimeZone.of("America/Los_Angeles")),
                TickingValue(
                    value = when (TEST_PLATFORM) {
                        // Web platforms don't support default LOCATION time zone style
                        is Js, is Wasm -> "this year, Pacific Time"
                        else -> "this year, Los Angeles Time"
                    },
                    nextTick = 214.days
                ),
            ),
            tuple(
                DynamicZonedYearLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicZonedYearOptions(
                        relativeOptions = RelativeZonedYearOptions(
                            yearOptions = RelativeYearOptions(style = RelativeUnitStyle.SHORT),
                            timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.ID),
                        ),
                    ),
                ),
                Zoned(2025, TimeZone.UTC),
                TickingValue("last yr., UTC", nextTick = 214.days),
            ),
        ) { (localizer, year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW) shouldBeLocalizedAs expected
        }
    }

    context("custom threshold") {
        val localizer = DynamicZonedYearLocalizer(
            locale = LOCALE_ITALIAN,
            options = DynamicZonedYearOptions(
                relativeDiffRange = -10..5,
                relativeOptions = RelativeZonedYearOptions(timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.ID)),
                absoluteOptions = ZonedYearOptions(
                    timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.ID)
                )
            )
        )

        withTests(
            nameFn = { it.toString() },
            Zoned(2015, TimeZone.of("America/Los_Angeles")) to "2015, America/Los_Angeles",
            Zoned(2016, TimeZone.of("Europe/Rome")) to "10 anni fa, Europe/Rome",
            Zoned(2026, TimeZone.of("America/New_York")) to "quest’anno, America/New_York",
            Zoned(2031, TimeZone.of("America/Los_Angeles")) to "tra 5 anni, America/Los_Angeles",
            Zoned(2032, TimeZone.of("Europe/Lisbon")) to "2032, Europe/Lisbon",
        ) { (year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW).value shouldBeLocalizedAs expected
        }
    }

    context("nextTick") {
        val localizer = DynamicZonedYearLocalizer(locale = LOCALE_ENGLISH)

        localizer.nextTickPredictsChangeTest(
            arbitraryArb = Arb.zoned(Arb.year()),
            smallArb = { zonedInstant -> Arb.element(zonedInstant.mapValue { zonedInstant.toLocalDateTime().year }) },
            referenceRange = DEFAULT_INSTANT_RANGE,
        )
    }
}

class DynamicZonedYearLocalizerTest : FunSpec({
    include(DynamicZonedYearLocalizerTestFactory)
})
