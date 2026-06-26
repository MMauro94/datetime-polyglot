@file:OptIn(ExperimentalMultiUnitRelativeDuration::class)

package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.localizers.absolute.DurationOptions
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.duration
import io.kotest.property.checkAll
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class MultiUnitRelativeDurationLocalizerTest : FunSpec({
    context("format") {
        // These are just basic smoke tests, as thorough testing is already carried out in DurationLocalizerTest
        // for the format and remainderUntilNextUnitBoundary for the next tick
        context("basic tests") {
            withTests(
                nameFn = { Pair(it.b, it.c).toString() },
                tuple(
                    MultiUnitRelativeDurationLocalizer(
                        options = DurationOptions(style = DurationStyle.WIDE),
                        locale = LOCALE_ENGLISH
                    ),
                    1.days + 5.hours,
                    TickingValue("1 day, 5 hours", nextTick = 1.nanoseconds),
                ),
                tuple(
                    MultiUnitRelativeDurationLocalizer(
                        options = DurationOptions(style = DurationStyle.NARROW),
                        locale = LOCALE_ENGLISH
                    ),
                    5.hours + 15.minutes + 43.seconds,
                    TickingValue("5h 15m", nextTick = 43.seconds + 1.nanoseconds),
                ),
                tuple(
                    MultiUnitRelativeDurationLocalizer(
                        options = DurationOptions(style = DurationStyle.WIDE),
                        locale = LOCALE_ITALIAN
                    ),
                    -(6.hours + 13.seconds),
                    TickingValue("6 ore", nextTick = 47.seconds),
                ),
                tuple(
                    MultiUnitRelativeDurationLocalizer(
                        options = DurationOptions(style = DurationStyle.NARROW, minUnit = DurationUnit.SECONDS),
                        locale = LOCALE_ENGLISH
                    ),
                    -(4.minutes + 22.seconds + 440.milliseconds + 141.microseconds),
                    TickingValue("4m 22s", nextTick = 559.milliseconds + 859.microseconds),
                ),
                tuple(
                    MultiUnitRelativeDurationLocalizer(
                        options = DurationOptions(style = DurationStyle.NARROW, minUnit = DurationUnit.MINUTES),
                        locale = LOCALE_ENGLISH
                    ),
                    10.seconds,
                    TickingValue("0m", nextTick = 1.minutes + 10.seconds),
                ),
                tuple(
                    MultiUnitRelativeDurationLocalizer(
                        options = DurationOptions(minUnit = DurationUnit.MINUTES, ifZeroLocalization = { "adesso" }),
                        locale = LOCALE_ITALIAN
                    ),
                    55.seconds,
                    TickingValue("adesso", nextTick = 1.minutes + 55.seconds),
                ),
            ) { (localizer, duration, expected) ->
                localizer.localizeAndTestNextTick(duration) shouldBe expected
            }
        }

        context("formatted string") {
            test("negative duration yields same format as positive") {
                val localizer = MultiUnitRelativeDurationLocalizer(locale = LOCALE_ENGLISH)
                checkAll(Arb.duration()) { duration ->
                    localizer.localizeAndTestNextTick(duration).value shouldBe localizer.localizeAndTestNextTick(-duration).value
                }
            }
        }

        context("nextTick") {
            val localizer = MultiUnitRelativeDurationLocalizer(locale = LOCALE_ENGLISH)
            test("should never be null") {
                checkAll(Arb.duration()) { duration ->
                    localizer.localize(duration).nextTick.shouldNotBeNull()
                }
            }

            nextTickPredictsChangeTest(
                arbitraryArb = Arb.duration(),
                smallArb = { it },
                advanceBy = Duration::minus,
                localize = localizer::localize,
            )
        }
    }
})

private fun MultiUnitRelativeDurationLocalizer.localizeAndTestNextTick(duration: Duration): TickingValue<String> {
    return localizeAndTestNextTick(
        params = duration,
        advanceBy = Duration::minus, // As time advances, the duration will go backwards
        localize = ::localize,
    )
}
