package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Android
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localeFromBcp47LanguageTag
import dev.mmauro.datetimepolyglot.localizers.localDate
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.tuple
import io.kotest.datatest.withContexts
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * Reference date used in most tests. This is a Wednesday
 */
private val REFERENCE_DATE = LocalDate.parse("2025-12-31")
private val REFERENCE = Zoned(REFERENCE_DATE.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)

val RelativeLocalDateLocalizerTestFactory = funSpec {
    context("localize") {
        context("localized string") {
            context("number of days") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(LOCALE_ENGLISH, LocalDate(2025, Month.AUGUST, 29), "124 days ago"),
                    tuple(LOCALE_ENGLISH, LocalDate(2025, Month.DECEMBER, 21), "10 days ago"),
                    tuple(LOCALE_ENGLISH, LocalDate(2025, Month.DECEMBER, 29), "2 days ago"),
                    tuple(LOCALE_ENGLISH, LocalDate(2026, Month.JANUARY, 15), "in 15 days"),
                    tuple(LOCALE_ENGLISH, LocalDate(2027, Month.JULY, 9), "in 555 days"),
                    tuple(LOCALE_ITALIAN, LocalDate(2025, Month.AUGUST, 29), "124 giorni fa"),
                    tuple(LOCALE_ITALIAN, LocalDate(2025, Month.DECEMBER, 21), "10 giorni fa"),
                    tuple(LOCALE_ITALIAN, LocalDate(2025, Month.DECEMBER, 28), "3 giorni fa"),
                    tuple(LOCALE_ITALIAN, LocalDate(2026, Month.JANUARY, 15), "tra 15 giorni"),
                    tuple(LOCALE_ITALIAN, LocalDate(2027, Month.JULY, 9), "tra 555 giorni"),
                ) { (locale, localDate, expected) ->
                    RelativeLocalDateLocalizer(locale = locale).localizeAndTestNextTick(localDate, REFERENCE).value shouldBe expected
                }
            }

            context("single word") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(LOCALE_ENGLISH, LocalDate(2025, Month.DECEMBER, 30), "yesterday"),
                    tuple(LOCALE_ENGLISH, REFERENCE_DATE, "today"),
                    tuple(LOCALE_ENGLISH, LocalDate(2026, Month.JANUARY, 1), "tomorrow"),
                    tuple(LOCALE_ITALIAN, LocalDate(2025, Month.DECEMBER, 29), "l’altro ieri"),
                    tuple(LOCALE_ITALIAN, LocalDate(2026, Month.JANUARY, 2), "dopodomani"),
                ) { (locale, localDate, expected) ->
                    RelativeLocalDateLocalizer(locale = locale).localizeAndTestNextTick(localDate, REFERENCE).value shouldBe expected
                }
            }

            context("up to 7 days in the future uses this/next day of week") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(LOCALE_ENGLISH, LocalDate(2026, Month.JANUARY, 2), "this Friday"),
                    tuple(LOCALE_ENGLISH, LocalDate(2026, Month.JANUARY, 3), "this Saturday"),
                    // English's first day of week is Sunday
                    tuple(LOCALE_ENGLISH, LocalDate(2026, Month.JANUARY, 4), "next Sunday"),
                    tuple(LOCALE_ENGLISH, LocalDate(2026, Month.JANUARY, 7), "next Wednesday"),
                    tuple(LOCALE_ENGLISH, LocalDate(2026, Month.JANUARY, 8), "in 8 days"),

                    tuple(LOCALE_ITALIAN, LocalDate(2026, Month.JANUARY, 3), "questo sabato"),
                    tuple(LOCALE_ITALIAN, LocalDate(2026, Month.JANUARY, 4), "questa domenica"),
                    // Italian's first day of week is Monday
                    tuple(LOCALE_ITALIAN, LocalDate(2026, Month.JANUARY, 5), "lunedì prossimo"),
                    tuple(LOCALE_ITALIAN, LocalDate(2026, Month.JANUARY, 7), "mercoledì prossimo"),
                ) { (locale, localDate, expected) ->
                    val localizer = RelativeLocalDateLocalizer(
                        locale = locale,
                        options = RelativeLocalDateOptions(useRelativeDayOfWeek = true),
                    )
                    localizer.localizeAndTestNextTick(localDate, REFERENCE).value shouldBe expected
                }
            }

            context("useRelativeDayOfWeek is respected") {
                withTests(
                    true to "next Sunday",
                    false to "in 4 days",
                ) { (useRelativeDayOfWeek, expected) ->
                    val localizer = RelativeLocalDateLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = RelativeLocalDateOptions(useRelativeDayOfWeek = useRelativeDayOfWeek),
                    )
                    localizer.localizeAndTestNextTick(LocalDate(2026, Month.JANUARY, 4), REFERENCE).value shouldBe expected
                }
            }

            context("number format follows locale convention") {
                withTests(
                    nameFn = { it.first.toString() },
                    LOCALE_ENGLISH to "1,234,567",
                    LOCALE_ITALIAN to "1.234.567",
                    localeFromBcp47LanguageTag("hi") to "12,34,567",
                ) { (locale, expected) ->
                    val localDate = REFERENCE_DATE - DatePeriod(days = 1_234_567)
                    RelativeLocalDateLocalizer(locale = locale).localizeAndTestNextTick(localDate, REFERENCE).value shouldContain expected
                }
            }

            context("different styles are respected") {
                withContexts(
                    nameFn = { it.toString() },
                    tuple(RelativeUnitStyle.LONG, -1, "yesterday"),
                    tuple(RelativeUnitStyle.LONG, 2, "this Friday"),
                    tuple(RelativeUnitStyle.LONG, 10, "in 10 days"),
                    tuple(RelativeUnitStyle.SHORT, -1, "yesterday"),
                    tuple(RelativeUnitStyle.SHORT, 2, "this Fri."),
                    tuple(RelativeUnitStyle.SHORT, 10, "in 10 days"),
                    tuple(RelativeUnitStyle.NARROW, -1, "yesterday"),
                    tuple(RelativeUnitStyle.NARROW, 2, "this F"),
                    tuple(
                        RelativeUnitStyle.NARROW,
                        10,
                        when (val platform = TEST_PLATFORM) {
                            is Android if platform.sdk < 34 -> "in 10 days"
                            else -> "in 10d"
                        },
                    ),
                ) { (style, days, expected) ->
                    val localizer = RelativeLocalDateLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = RelativeLocalDateOptions(style = style, useRelativeDayOfWeek = true),
                    )
                    val localDate = REFERENCE_DATE + DatePeriod(days = days)
                    localizer.localizeAndTestNextTick(localDate, REFERENCE).value shouldBe expected
                }
            }
        }

        context("next tick") {
            val localizer = RelativeLocalDateLocalizer(
                locale = LOCALE_ENGLISH,
                options = RelativeLocalDateOptions(useRelativeDayOfWeek = true),
            )

            context("basic tests") {
                val date = LocalDate(2026, Month.JANUARY, 1)
                withTests(
                    nameFn = { "${it.a} ${it.b}" },
                    tuple(
                        Zoned(Instant.parse("2026-01-01T21:00:00Z"), TimeZone.UTC),
                        3.hours,
                    ),
                    tuple(
                        Zoned(Instant.parse("2026-01-01T23:59:59Z"), TimeZone.UTC),
                        1.seconds,
                    ),
                    tuple(
                        Zoned(Instant.parse("2026-01-01T22:35:41Z"), TimeZone.of("Europe/Berlin")),
                        24.minutes + 19.seconds,
                    ),
                    // This returns "this Thursday", which is valid for multiple days until "tomorrow" triggers
                    tuple(
                        Zoned(Instant.parse("2025-12-28T06:00:00Z"), TimeZone.UTC),
                        2.days + 18.hours,
                    ),
                ) { (now, expected) ->
                    localizer.localizeAndTestNextTick(date, now).nextTick shouldBe expected
                }
            }

            context("handles DST changes") {
                val tz = TimeZone.of("Europe/Rome")
                test("jump forward") {
                    val date = LocalDate.parse("2025-03-30")
                    localizer.localizeAndTestNextTick(
                        value = date,
                        reference = Zoned(date.atStartOfDayIn(tz), tz),
                    ).nextTick shouldBe 23.hours
                }
                test("jump backward") {
                    val date = LocalDate.parse("2025-10-26")
                    localizer.localizeAndTestNextTick(
                        value = date,
                        reference = Zoned(date.atStartOfDayIn(tz), tz),
                    ).nextTick shouldBe 25.hours
                }
            }
            localizer.nextTickPredictsChangeTest(
                arbitraryArb = Arb.localDate(),
                smallArb = { Arb.element(it.toLocalDateTime().date) },
            )
        }
    }
}

class RelativeLocalDateLocalizerTest : FunSpec({
    include(RelativeLocalDateLocalizerTestFactory)
})
