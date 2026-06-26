package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.datatest.withContexts
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

val DurationLocalizerTestFactory = funSpec {
    context("options") {
        context("maxUnits must be positive") {
            withTests(-10, -2, -1, 0) { maxUnits ->
                shouldThrow<IllegalArgumentException> {
                    DurationOptions(
                        omitZeros = true,
                        maxUnits = maxUnits,
                    )
                }
            }
        }
    }

    context("localize") {
        suspend fun FunSpecContainerScope.runTestCases(
            omitZeros: List<Boolean>,
            minUnit: List<DurationUnit>,
            maxUnits: List<Int>,
            duration: Duration,
            locale: PlatformLocale,
            expected: String,
        ) {
            checkAll(omitZeros.exhaustive(), minUnit.exhaustive(), maxUnits.exhaustive()) { omitZeros, minUnit, maxUnits ->
                val options = DurationOptions(
                    omitZeros = omitZeros,
                    minUnit = minUnit,
                    maxUnits = maxUnits,
                    width = DurationStyle.WIDE,
                )
                test(options.toString()) {
                    duration.localize(options, locale) shouldBeLocalizedAs expected
                }
            }
        }

        context("invalid durations") {
            withTests(
                Duration.INFINITE,
                -Duration.INFINITE,
                -1.nanoseconds,
                -5.minutes
            ) { duration ->
                shouldThrow<IllegalArgumentException> {
                    duration.localize()
                }
            }
        }

        context("maxUnits is respected") {
            context("maxUnit = 2") {
                runTestCases(
                    omitZeros = listOf(true, false),
                    minUnit = listOf(DurationUnit.MINUTES, DurationUnit.SECONDS, DurationUnit.MILLISECONDS),
                    maxUnits = listOf(2),
                    duration = 5.hours + 3.minutes + 10.seconds + 150.milliseconds,
                    locale = LOCALE_ENGLISH,
                    expected = "5 hours, 3 minutes",
                )
            }
            context("maxUnit = 1") {
                runTestCases(
                    omitZeros = listOf(true, false),
                    minUnit = listOf(DurationUnit.HOURS, DurationUnit.MINUTES, DurationUnit.SECONDS),
                    maxUnits = listOf(1),
                    duration = 5.hours + 10.minutes + 40.seconds,
                    locale = LOCALE_ENGLISH,
                    expected = "5 hours",
                )
            }
            context("omitted zero units are considered for maxUnit") {
                runTestCases(
                    omitZeros = listOf(true),
                    minUnit = listOf(DurationUnit.MINUTES, DurationUnit.SECONDS, DurationUnit.MILLISECONDS, DurationUnit.NANOSECONDS),
                    maxUnits = listOf(3),
                    duration = 5.hours + 150.milliseconds,
                    locale = LOCALE_ENGLISH,
                    expected = "5 hours",
                )
            }
        }

        context("enabled omit zeros avoids 0 middle units") {
            runTestCases(
                omitZeros = listOf(true),
                minUnit = listOf(DurationUnit.MILLISECONDS, DurationUnit.NANOSECONDS),
                maxUnits = listOf(5),
                duration = 5.hours + 250.milliseconds,
                locale = LOCALE_ENGLISH,
                expected = "5 hours, 250 milliseconds",
            )
        }

        context("disabled omit zeros will make relevant unit show up as zero") {
            runTestCases(
                omitZeros = listOf(false),
                minUnit = listOf(DurationUnit.MINUTES, DurationUnit.SECONDS),
                maxUnits = listOf(2),
                duration = 1.hours + 150.milliseconds,
                locale = LOCALE_ENGLISH,
                expected = "1 hour, 0 minutes",
            )
        }

        context("minUnit higher than duration causes zero min unit output") {
            runTestCases(
                omitZeros = listOf(true, false),
                minUnit = listOf(DurationUnit.MINUTES),
                maxUnits = listOf(1, 2),
                duration = 59.seconds,
                locale = LOCALE_ENGLISH,
                expected = "0 minutes",
            )
        }

        context("zero duration outputs 0 with min unit") {
            runTestCases(
                omitZeros = listOf(true, false),
                minUnit = listOf(DurationUnit.MINUTES),
                maxUnits = listOf(1, 2, 3),
                duration = Duration.ZERO,
                locale = LOCALE_ENGLISH,
                expected = "0 minutes",
            )
            runTestCases(
                omitZeros = listOf(true, false),
                minUnit = listOf(DurationUnit.HOURS),
                maxUnits = listOf(1, 2, 3),
                duration = Duration.ZERO,
                locale = LOCALE_ENGLISH,
                expected = "0 hours",
            )
        }

        context("duration width") {
            withTests(
                DurationStyle.NARROW to "1h 5m",
                DurationStyle.SHORT to "1 hr, 5 min",
                DurationStyle.WIDE to "1 hour, 5 minutes",
            ) { (durationWidth, expected) ->
                (1.hours + 5.minutes).localize(
                    options = DurationOptions(width = durationWidth),
                    locale = LOCALE_ENGLISH,
                ) shouldBeLocalizedAs expected
            }
        }

        context("works in a different language") {
            withTests(
                nameFn = { it.toString() },
                LOCALE_ENGLISH to "1 hour, 5 minutes",
                LOCALE_ITALIAN to "1 ora e 5 minuti",
                LOCALE_POLISH to "1 godzina i 5 minut",
            ) { (locale, expected) ->
                (1.hours + 5.minutes).localize(locale = locale) shouldBeLocalizedAs expected
            }
        }
    }


    context("detectUnits") {
        test("no more than maxUnit units are returned") {
            val options = DurationOptions(
                omitZeros = true,
                minUnit = DurationUnit.MILLISECONDS,
                maxUnits = 3,
            )
            options.detectUnits(5.hours + 15.seconds) shouldBe listOf(
                5L to DurationUnit.HOURS,
                0L to DurationUnit.MINUTES,
                15L to DurationUnit.SECONDS,
            )
        }

        test("not enough units to fulfill max units") {
            val formatter = DurationOptions(
                omitZeros = true,
                minUnit = DurationUnit.MINUTES,
                maxUnits = 2,
            )
            formatter.detectUnits(40.minutes) shouldBe listOf(
                40L to DurationUnit.MINUTES,
            )
        }

        test("duration is smaller than 1 minUnit") {
            val formatter = DurationOptions(
                omitZeros = true,
                minUnit = DurationUnit.HOURS,
                maxUnits = 2,
            )
            formatter.detectUnits(59.minutes + 59.seconds) shouldBe listOf(
                0L to DurationUnit.HOURS,
            )
        }

        context("a zero duration returns a single unit equal to minUnit") {
            withContexts(DurationUnit.entries) { minUnit ->
                withTests(nameFn = { "maxUnits=$it" }, 1, 2, 3) { maxUnits ->
                    val formatter = DurationOptions(
                        omitZeros = true,
                        minUnit = minUnit,
                        maxUnits = maxUnits,
                    )
                    formatter.detectUnits(Duration.ZERO) shouldBe listOf(0L to minUnit)
                }
            }
        }
    }
}

class DurationLocalizerTest : FunSpec({
    include(DurationLocalizerTestFactory)
})