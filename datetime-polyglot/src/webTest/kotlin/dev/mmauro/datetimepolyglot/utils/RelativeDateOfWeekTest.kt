package dev.mmauro.datetimepolyglot.utils

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.localeFromBcp47LanguageTag
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeDirection
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek

class RelativeDateOfWeekTest : FunSpec({
    context("directions") {
        withTests(RelativeDirection.entries) { direction ->
            val localized = localizeRelativeDayOfWeek(
                locale = LOCALE_ENGLISH,
                style = RelativeUnitStyle.LONG,
                direction = direction,
                dayOfWeek = DayOfWeek.MONDAY,
            )
            localized shouldBe when (direction) {
                RelativeDirection.LAST_2 -> null
                RelativeDirection.LAST -> "last Monday"
                RelativeDirection.THIS -> "this Monday"
                RelativeDirection.NEXT -> "next Monday"
                RelativeDirection.NEXT_2 -> null
            }
        }
    }

    context("styles") {
        withTests(RelativeUnitStyle.entries) { style ->
            val localized = localizeRelativeDayOfWeek(
                locale = LOCALE_ENGLISH,
                style = style,
                direction = RelativeDirection.LAST,
                dayOfWeek = DayOfWeek.SUNDAY,
            )
            localized shouldBe when (style) {
                RelativeUnitStyle.NARROW -> "last Su"
                RelativeUnitStyle.SHORT -> "last Sun."
                RelativeUnitStyle.LONG -> "last Sunday"
            }
        }
    }

    test("Italian") {
        val localized = localizeRelativeDayOfWeek(
            locale = LOCALE_ITALIAN,
            style = RelativeUnitStyle.LONG,
            direction = RelativeDirection.NEXT,
            dayOfWeek = DayOfWeek.WEDNESDAY,
        )
        localized shouldBe "mercoledì prossimo"
    }

    context("works with big locales") {
        withTests(
            localeFromBcp47LanguageTag("it-Latn-IT-u-ca-gregory-fw-mon-hc-h23") to "mer. scorso",
            localeFromBcp47LanguageTag("en-US-u-ca-gregory-hc-h12-nu-latn") to "last Wed.",
            localeFromBcp47LanguageTag("en-GB-u-ca-gregory-hc-h12-nu-latn") to "last Wed",
        ) { (locale, expected) ->
            val localized = localizeRelativeDayOfWeek(
                locale = locale,
                style = RelativeUnitStyle.SHORT,
                direction = RelativeDirection.LAST,
                dayOfWeek = DayOfWeek.WEDNESDAY,
            )
            localized shouldBe expected
        }
    }
})
