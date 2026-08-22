package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import kotlinx.datetime.DayOfWeek

class RelativeUnitLocalizerTest : FunSpec({
    context("localizeNumeric") {
        test("past") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
            localizer.localizeNumeric(-1.0, RelativeUnit.DateTimeComponent.DAY) shouldBeLocalizedAs "1 day ago"
        }
        test("now") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
            localizer.localizeNumeric(0.0, RelativeUnit.DateTimeComponent.SECOND) shouldBeLocalizedAs "in 0 seconds"
        }
        test("future") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
            localizer.localizeNumeric(1.0, RelativeUnit.DateTimeComponent.DAY) shouldBeLocalizedAs "in 1 day"
        }

        context("styles") {
            withTests(RelativeUnitStyle.entries) { style ->
                val localizer = RelativeUnitLocalizer(style = style, locale = LOCALE_ENGLISH)
                localizer.localizeNumeric(-1.0, RelativeUnit.DateTimeComponent.MINUTE) shouldBeLocalizedAs when (style) {
                    RelativeUnitStyle.NARROW -> "1m ago"
                    RelativeUnitStyle.SHORT -> "1 min. ago"
                    RelativeUnitStyle.LONG -> "1 minute ago"
                }
            }
        }

        context("different locale") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.SHORT, locale = LOCALE_ITALIAN)
            localizer.localizeNumeric(5.0, RelativeUnit.DateTimeComponent.WEEK) shouldBeLocalizedAs "tra 5 sett."
        }
    }

    context("localizeDirection") {
        context("day") {
            withTests(RelativeDirection.entries) { direction ->
                val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
                localizer.localizeDiffDirection(direction, RelativeUnit.DateTimeComponent.DAY) shouldBe when (direction) {
                    RelativeDirection.LAST_2 -> null
                    RelativeDirection.LAST -> "yesterday"
                    RelativeDirection.THIS -> "today"
                    RelativeDirection.NEXT -> "tomorrow"
                    RelativeDirection.NEXT_2 -> null
                }
            }
        }
        context("year") {
            withTests(RelativeDirection.entries) { direction ->
                val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
                localizer.localizeDiffDirection(direction, RelativeUnit.DateTimeComponent.YEAR) shouldBe when (direction) {
                    RelativeDirection.LAST_2 -> null
                    RelativeDirection.LAST -> "last year"
                    RelativeDirection.THIS -> "this year"
                    RelativeDirection.NEXT -> "next year"
                    RelativeDirection.NEXT_2 -> null
                }
            }
        }

        context("second") {
            test("this second localization should return null") {
                checkAll(Arb.element(LOCALE_ENGLISH, LOCALE_ITALIAN, LOCALE_POLISH), Arb.enum<RelativeUnitStyle>()) { locale, style ->
                    val localizer = RelativeUnitLocalizer(style, locale)
                    localizer.localizeDiffDirection(RelativeDirection.THIS, RelativeUnit.DateTimeComponent.SECOND).shouldBeNull()
                }
            }
        }

        context("day of the week") {
            withTests(RelativeDirection.entries) { direction ->
                val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
                localizer.localizeDiffDirection(direction, RelativeUnit.DayOfWeek(DayOfWeek.WEDNESDAY)) shouldBe when (direction) {
                    RelativeDirection.LAST_2 -> null
                    RelativeDirection.LAST -> "last Wednesday"
                    RelativeDirection.THIS -> "this Wednesday"
                    RelativeDirection.NEXT -> "next Wednesday"
                    RelativeDirection.NEXT_2 -> null
                }
            }
        }

        context("styles") {
            withTests(RelativeUnitStyle.entries) { style ->
                val localizer = RelativeUnitLocalizer(style = style, locale = LOCALE_ENGLISH)
                localizer.localizeDiffDirection(RelativeDirection.LAST, RelativeUnit.DateTimeComponent.MONTH)
                    .shouldNotBeNull() shouldBeLocalizedAs when (style) {
                    RelativeUnitStyle.NARROW -> "last mo."
                    RelativeUnitStyle.SHORT -> "last mo."
                    RelativeUnitStyle.LONG -> "last month"
                }
            }
        }

        context("different locale") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ITALIAN)
            localizer.localizeDiffDirection(RelativeDirection.LAST_2, RelativeUnit.DateTimeComponent.DAY)
                .shouldNotBeNull() shouldBeLocalizedAs "l’altro ieri"
        }

        test("should never be formatted identically as localizeNumeric") {
            checkAll(
                Arb.enum<RelativeDirection>(),
                Arb.enum<RelativeUnit.DateTimeComponent>(),
                Arb.enum<RelativeUnitStyle>(),
                listOf(LOCALE_ENGLISH, LOCALE_ITALIAN, LOCALE_POLISH).exhaustive(),
            ) { direction, relativeUnit, style, locale ->
                val localizer = RelativeUnitLocalizer(style, locale)

                localizer.localizeDiffDirection(direction, relativeUnit)
                    .shouldNotBe(localizer.localizeNumeric(direction.offset.toDouble(), relativeUnit))
            }
        }
    }

    context("localizeNow") {
        withTests(
            nameFn = { it.first.toString() },
            LOCALE_ENGLISH to "now",
            LOCALE_ITALIAN to "ora",
            LOCALE_POLISH to "teraz",
        ) { (locale, expected) ->
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale)
            localizer.localizeNow() shouldBe expected
        }
    }
})
