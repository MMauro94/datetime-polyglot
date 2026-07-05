package dev.mmauro.datetimepolyglot

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek

class LocaleTest : FunSpec({
    context("base locale") {
        withTests(
            nameFn = { it.first },
            "zh-Hant-CN-x-private1-private2" to "zh-Hant-CN",
            "it-IT" to "it-IT",
            "it-Latn-IT" to "it-Latn-IT",
            "en" to "en",
        ) { (locale, expected) ->
            localeFromBcp47LanguageTag(locale).baseLocale.bcp47LanguageTag shouldBe expected
        }
    }

    context("firstDayOfWeek") {
        withTests(
            "en-US" to DayOfWeek.SUNDAY,
            "it" to DayOfWeek.MONDAY,
            "en-IE" to DayOfWeek.MONDAY,
            "it-u-fw-wed" to DayOfWeek.WEDNESDAY,
        ) { (locale, expected) ->
            localeFromBcp47LanguageTag(locale).firstDayOfWeek shouldBe expected
        }
    }

    context("rfc4647Lookup") {
        withTests(
            nameFn = { it.first },
            "zh-Hant-CN-x-private1-private2" to listOf(
                "zh-Hant-CN-x-private1",
                "zh-Hant-CN",
                "zh-Hant",
                "zh",
            ),
            "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper-kn-nu-latn-rg-dezzzz-tz-deber" to listOf(
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper-kn-nu-latn-rg-dezzzz-tz",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper-kn-nu-latn-rg-dezzzz",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper-kn-nu-latn-rg",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper-kn-nu-latn",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper-kn-nu",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper-kn",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf-upper",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23-kf",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc-h23",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk-hc",
                "de-Latn-DE-1901-u-ca-gregory-co-phonebk",
                "de-Latn-DE-1901-u-ca-gregory-co",
                "de-Latn-DE-1901-u-ca-gregory",
                "de-Latn-DE-1901-u-ca",
                "de-Latn-DE-1901",
                "de-Latn-DE",
                "de-Latn",
                "de",
            ),
            "it-Latn-IT" to listOf("it-Latn", "it"),
            "it-IT" to listOf("it"),
            "en" to emptyList(),
        ) { (locale, expected) ->
            localeFromBcp47LanguageTag(locale).rfc4647Lookup().map { it.bcp47LanguageTag }.toList() shouldBe expected
        }
    }
})