package dev.mmauro.datetimepolyglot.utils

import dev.mmauro.datetimepolyglot.ALL_LOCALES
import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.localeFromBcp47LanguageTag
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import kotlin.uuid.Uuid

class DateTimeJoinerTest : FunSpec({
    context("English") {
        withData(DateStyle.entries) { style ->
            joinDateAndTime(
                locale = LOCALE_ENGLISH,
                style = style,
                date = "DATE",
                time = "TIME",
            ) shouldBeLocalizedAs when (style) {
                DateStyle.SHORT -> "DATE, TIME"
                DateStyle.MEDIUM -> "DATE, TIME"
                DateStyle.LONG -> "DATE at TIME"
                DateStyle.FULL -> "DATE at TIME"
            }
        }
    }

    context("Italian") {
        withData(DateStyle.entries) { style ->
            joinDateAndTime(
                locale = LOCALE_ITALIAN,
                style = style,
                date = "DATA",
                time = "ORA",
            ) shouldBeLocalizedAs when (style) {
                DateStyle.SHORT -> "DATA, ORA"
                DateStyle.MEDIUM -> "DATA, ORA"
                DateStyle.LONG -> "DATA alle ore ORA"
                DateStyle.FULL -> "DATA alle ore ORA"
            }
        }
    }

    test("with realistic date/time values") {
        joinDateAndTime(
            locale = LOCALE_ENGLISH,
            style = DateStyle.FULL,
            date = "Thursday, January 8, 2026",
            time = "9:31:45 PM",
        ) shouldBeLocalizedAs "Thursday, January 8, 2026 at 9:31:45 PM"
    }

    context("works with big locales") {
        withTests(
            nameFn = { it.first.toString() },
            localeFromBcp47LanguageTag("it-Latn-IT-u-ca-gregory-fw-mon-hc-h23") to "ddddd alle ore ttttt",
            localeFromBcp47LanguageTag("en-US-u-ca-gregory-hc-h12-nu-latn") to "ddddd at ttttt",
            localeFromBcp47LanguageTag("es-VE-u-ca-gregory-hc-h12-nu-latn") to "ddddd a las ttttt",
        ) { (locale, expected) ->
            val localized = joinDateAndTime(
                locale = locale,
                style = DateStyle.LONG,
                date = "ddddd",
                time = "ttttt",
            )
            localized shouldBe expected
        }
    }

    test("date and time should always be present in the output string") {
        val date = Uuid.random().toString()
        val time = Uuid.random().toString()
        checkAll(Arb.element(ALL_LOCALES), Arb.enum<DateStyle>()) { locale, style ->
            val result = joinDateAndTime(
                locale = locale,
                style = style,
                date = date,
                time = time,
            )
            result shouldContain date
            result shouldContain time
        }
    }
})