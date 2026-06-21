package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle.Generic.ID
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle.Generic.LOCATION
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle.Generic.NON_LOCATION_LONG
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle.Generic.NON_LOCATION_SHORT
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withTests
import kotlinx.datetime.TimeZone

val TimeZoneLocalizerTestFactory = funSpec {
    data class TestCase(
        val timeZone: String,
        val locale: PlatformLocale,
        val expectedFn: (TimeZoneStyle.Generic) -> String,
    )

    withContexts(
        nameFn = { "${it.timeZone} in ${it.locale}" },
        TestCase("America/Los_Angeles", LOCALE_ENGLISH) {
            when (it) {
                ID -> "America/Los_Angeles"
                NON_LOCATION_SHORT -> "PT"
                NON_LOCATION_LONG -> "Pacific Time"
                LOCATION -> "Los Angeles Time"
            }
        },
        TestCase("Europe/Rome", LOCALE_ITALIAN) {
            when (it) {
                ID -> "Europe/Rome"
                NON_LOCATION_SHORT -> "CET"
                NON_LOCATION_LONG -> "Ora dell’Europa centrale"
                LOCATION -> "Ora Italia"
            }
        },
        TestCase("Etc/GMT+8", LOCALE_ENGLISH) {
            when (it) {
                ID -> "Etc/GMT+8"
                NON_LOCATION_SHORT -> "GMT-8"
                NON_LOCATION_LONG, LOCATION -> "GMT-08:00"
            }
        },
        TestCase("Africa/Tripoli", LOCALE_ENGLISH) {
            when (it) {
                ID -> "Africa/Tripoli"
                NON_LOCATION_SHORT -> "Libya Time"
                NON_LOCATION_LONG -> "Eastern European Standard Time"
                LOCATION -> "Libya Time"
            }
        },
        TestCase("Asia/Seoul", LOCALE_ITALIAN) {
            when (it) {
                ID -> "Asia/Seoul"
                NON_LOCATION_SHORT -> "Ora Corea del Sud"
                NON_LOCATION_LONG -> "Ora standard coreana"
                LOCATION -> "Ora Corea del Sud"
            }
        },
        TestCase("UTC", LOCALE_ITALIAN) {
            when (it) {
                ID -> "UTC"
                NON_LOCATION_SHORT -> when (TEST_PLATFORM) {
                    // Older versions of ICU (<78) bundled in Android only output "GMT"
                    is TestPlatform.Android -> "GMT"
                    else -> "GMT+0"
                }

                NON_LOCATION_LONG, LOCATION -> when (TEST_PLATFORM) {
                    // Older versions of ICU (<78) bundled in Android only output "GMT"
                    is TestPlatform.Android -> "GMT"
                    else -> "GMT+00:00"
                }
            }
        },
        TestCase("Universal", LOCALE_ENGLISH) {
            when (it) {
                ID -> "Universal"
                NON_LOCATION_SHORT -> when (TEST_PLATFORM) {
                    // Older versions of ICU (<78) bundled in Android only output "GMT"
                    is TestPlatform.Android -> "GMT"
                    else -> "GMT+0"
                }

                NON_LOCATION_LONG, LOCATION -> when (TEST_PLATFORM) {
                    // Older versions of ICU (<78) bundled in Android only output "GMT"
                    is TestPlatform.Android -> "GMT"
                    else -> "GMT+00:00"
                }
            }
        }
    ) { (timeZone, locale, expectedFn) ->
        withTests(TimeZoneStyle.Generic.entries) { timeZoneStyle ->
            val localized = TimeZone.of(timeZone).localize(
                options = TimeZoneOptions(timeZoneStyle),
                locale = locale
            )
            localized shouldBeLocalizedAs when (timeZoneStyle) {
                ID, NON_LOCATION_SHORT, NON_LOCATION_LONG -> expectedFn(timeZoneStyle)
                LOCATION -> when (TEST_PLATFORM) {
                    is TestPlatform.Js, is TestPlatform.Wasm -> expectedFn(NON_LOCATION_LONG)
                    else -> expectedFn(timeZoneStyle)
                }
            }
        }
    }
}

class TimeZoneLocalizerTest : FunSpec({
    include(TimeZoneLocalizerTestFactory)
})