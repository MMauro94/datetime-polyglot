package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Android
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localeFromBcp47LanguageTag
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.year
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
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
import kotlin.time.Instant

private val REFERENCE_DATE = LocalDate.parse("2026-04-01")
private val REFERENCE = Zoned(REFERENCE_DATE.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)

val RelativeYearLocalizerTestFactory = funSpec {
    context("localizer") {
        context("localized string") {
            context("numeric") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(LOCALE_ENGLISH, 1990, "36 years ago"),
                    tuple(LOCALE_ENGLISH, 2020, "6 years ago"),
                    tuple(LOCALE_ENGLISH, 2024, "2 years ago"),
                    tuple(LOCALE_ENGLISH, 2028, "in 2 years"),
                    tuple(LOCALE_ENGLISH, 2100, "in 74 years"),
                    tuple(LOCALE_ITALIAN, 1990, "36 anni fa"),
                    tuple(LOCALE_ITALIAN, 2020, "6 anni fa"),
                    tuple(LOCALE_ITALIAN, 2030, "tra 4 anni"),
                ) { (locale, year, expected) ->
                    RelativeYearLocalizer(locale = locale).localizeAndTestNextTick(year, REFERENCE).value shouldBe expected
                }
            }

            context("single word") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(LOCALE_ENGLISH, 2025, "last year"),
                    tuple(LOCALE_ENGLISH, 2026, "this year"),
                    tuple(LOCALE_ENGLISH, 2027, "next year"),
                    tuple(LOCALE_ITALIAN, 2025, "anno scorso"),
                    tuple(LOCALE_ITALIAN, 2026, "quest’anno"),
                    tuple(LOCALE_ITALIAN, 2027, "anno prossimo"),
                ) { (locale, year, expected) ->
                    RelativeYearLocalizer(locale = locale).localizeAndTestNextTick(year, REFERENCE).value shouldBe expected
                }
            }

            context("number format follows locale convention") {
                withTests(
                    nameFn = { it.first.toString() },
                    LOCALE_ENGLISH to "in 1,000,000 years",
                    LOCALE_ITALIAN to "tra 1.000.000 anni",
                    localeFromBcp47LanguageTag("hi") to "10,00,000 वर्ष में",
                ) { (locale, expected) ->
                    RelativeYearLocalizer(locale = locale).localizeAndTestNextTick(1_002_026, REFERENCE).value shouldBe expected
                }
            }

            context("styles") {
                context("numeric") {
                    withTests(RelativeUnitStyle.entries) { style ->
                        val localizer = RelativeYearLocalizer(locale = LOCALE_ENGLISH, options = RelativeYearOptions(style = style))
                        localizer.localizeAndTestNextTick(2030, REFERENCE).value shouldBe when (style) {
                            RelativeUnitStyle.NARROW -> when (val platform = TEST_PLATFORM) {
                                is Android if platform.sdk < 34 -> "in 4 yr."
                                else -> "in 4y"
                            }

                            RelativeUnitStyle.SHORT -> "in 4 yr."
                            RelativeUnitStyle.LONG -> "in 4 years"
                        }
                    }
                }
                context("word") {
                    withTests(RelativeUnitStyle.entries) { style ->
                        val localizer = RelativeYearLocalizer(locale = LOCALE_ENGLISH, options = RelativeYearOptions(style = style))
                        localizer.localizeAndTestNextTick(2026, REFERENCE).value shouldBe when (style) {
                            RelativeUnitStyle.NARROW -> "this yr."
                            RelativeUnitStyle.SHORT -> "this yr."
                            RelativeUnitStyle.LONG -> "this year"
                        }
                    }
                }
            }

            context("allowedDirections is respected") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(emptySet(), 2025, "1 year ago"),
                    tuple(setOf(RelativeDirection.LAST), 2025, "last year"),
                    tuple(setOf(RelativeDirection.LAST), 2027, "in 1 year"),
                ) { (allowedDirections, year, expected) ->
                    val localizer = RelativeYearLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = RelativeYearOptions(allowedDirections = allowedDirections),
                    )
                    localizer.localizeAndTestNextTick(year, REFERENCE).value shouldBe expected
                }
            }
        }
    }

    test("localizeDiff") {
        RelativeYearLocalizer(locale = LOCALE_ENGLISH).localizeDiff(-1) shouldBeLocalizedAs "last year"
    }

    context("next tick") {
        val localizer = RelativeYearLocalizer(locale = LOCALE_ENGLISH)

        nextTickPredictsChangeTest(
            arbitraryArb = Arb.year(),
            smallArb = { Arb.element(it.toLocalDateTime().year) },
            localize = localizer::localize,
        )
    }
}

class RelativeYearLocalizerTest : FunSpec({
    include(RelativeYearLocalizerTestFactory)
})

private fun RelativeYearLocalizer.localizeAndTestNextTick(year: Int, reference: Zoned<Instant>) =
    localizeAndTestNextTick(year, reference, ::localize)