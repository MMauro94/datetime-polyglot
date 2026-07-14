@file:OptIn(ExperimentalZonedLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Js
import dev.mmauro.datetimepolyglot.TestPlatform.Wasm
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.ExperimentalZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.localizers.year
import dev.mmauro.datetimepolyglot.localizers.zoned
import dev.mmauro.datetimepolyglot.mapValue
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.tuple
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days

private val REFERENCE_DATE = LocalDate.parse("2026-04-01")
private val REFERENCE = Zoned(REFERENCE_DATE.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)

val RelativeZonedYearLocalizerTestFactory = funSpec {
    context("localize") {
        withTests(
            nameFn = { Pair(it.b, it.c).toString() },
            tuple(
                RelativeZonedYearLocalizer(locale = LOCALE_ENGLISH),
                Zoned(value = 2050, timeZone = TimeZone.of("America/New_York")),
                TickingValue(
                    value = when (TEST_PLATFORM) {
                        // Web platforms don't support the "location" time zone style
                        is Js, is Wasm -> "in 24 years, Eastern Time"
                        else -> "in 24 years, New York Time"
                    },
                    nextTick = 275.days
                ),
            ),
            tuple(
                RelativeZonedYearLocalizer(
                    locale = LOCALE_ITALIAN,
                    options = RelativeZonedYearOptions(
                        timeZoneOptions = TimeZoneOptions(TimeZoneStyle.Generic.NON_LOCATION_LONG)
                    ),
                ),
                Zoned(
                    value = 2026,
                    timeZone = TimeZone.of("Europe/Rome"),
                ),
                TickingValue("quest’anno, Ora dell’Europa centrale", nextTick = 275.days),
            ),
        ) { (localizer, localDateTime, expected) ->
            localizer.localizeAndTestNextTick(localDateTime, REFERENCE) shouldBe expected
        }
    }


    context("next tick") {
        val localizer = RelativeZonedYearLocalizer(locale = LOCALE_ENGLISH)

        localizer.nextTickPredictsChangeTest(
            arbitraryArb = Arb.zoned(Arb.year()),
            smallArb = { zonedInstant -> Arb.element(zonedInstant.mapValue { zonedInstant.toLocalDateTime().year }) },
        )
    }
}

class RelativeZonedYearLocalizerTest : FunSpec({
    include(RelativeZonedYearLocalizerTestFactory)
})
