package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Android
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.property.Arb
import io.kotest.property.arbitrary.duration
import io.kotest.property.checkAll
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

val RelativeDurationLocalizerTestFactory = funSpec {
    context("positive duration") {
        val localizer = RelativeDurationLocalizer(locale = LOCALE_ENGLISH)
        localizer.localizeAndTestNextTick(2.hours) shouldBeLocalizedAs TickingValue(
            value = "in 2 hours",
            nextTick = 1.nanoseconds,
        )
    }

    context("zero duration") {
        val localizer = RelativeDurationLocalizer(locale = LOCALE_ENGLISH, options = RelativeDurationOptions(ifZeroLocalization = { null }))
        localizer.localizeAndTestNextTick(Duration.ZERO) shouldBeLocalizedAs TickingValue(
            value = "in 0 seconds",
            nextTick = 1.seconds,
        )
    }

    context("negative duration") {
        val localizer = RelativeDurationLocalizer(locale = LOCALE_ENGLISH)
        localizer.localizeAndTestNextTick(-5.minutes) shouldBeLocalizedAs TickingValue(
            value = "5 minutes ago",
            nextTick = 1.minutes,
        )
    }

    context("min unit") {
        withTests(
            DurationUnit.SECONDS to TickingValue("in 1 minute", nextTick = 3.seconds + 1.nanoseconds),
            DurationUnit.MINUTES to TickingValue("in 1 minute", nextTick = 3.seconds + 1.nanoseconds),
            DurationUnit.HOURS to TickingValue("in 0 hours", nextTick = 1.hours + 1.minutes + 3.seconds),
        ) { (unit, expected) ->
            val localizer = RelativeDurationLocalizer(
                options = RelativeDurationOptions(minUnit = unit),
                locale = LOCALE_ENGLISH
            )
            localizer.localizeAndTestNextTick(1.minutes + 3.seconds) shouldBeLocalizedAs expected
        }
    }

    context("max unit") {
        withTests(
            nameFn = { it.first.toString() },
            null to TickingValue(
                value = "in 2 days",
                nextTick = 3.hours + 5.minutes + 4.seconds + 250.milliseconds + 1.nanoseconds
            ),
            DurationUnit.HOURS to TickingValue(
                value = "in 51 hours",
                nextTick = 5.minutes + 4.seconds + 250.milliseconds + 1.nanoseconds
            ),
            DurationUnit.MINUTES to TickingValue(
                value = "in 3,065 minutes",
                nextTick = 4.seconds + 250.milliseconds + 1.nanoseconds
            ),
            DurationUnit.SECONDS to TickingValue(
                value = "in 183,904 seconds",
                nextTick = 250.milliseconds + 1.nanoseconds
            ),
        ) { (unit, expected) ->
            val localizer = RelativeDurationLocalizer(
                options = RelativeDurationOptions(maxUnit = unit),
                locale = LOCALE_ENGLISH,
            )
            localizer.localizeAndTestNextTick(2.days + 3.hours + 5.minutes + 4.seconds + 250.milliseconds) shouldBeLocalizedAs expected
        }
    }

    context("styles") {
        withTests(RelativeUnitStyle.entries) { style ->
            val localizer = RelativeDurationLocalizer(
                options = RelativeDurationOptions(style = style),
                locale = LOCALE_ENGLISH,
            )
            localizer.localizeAndTestNextTick(-(4.hours + 5.minutes)).value shouldBeLocalizedAs when (style) {
                RelativeUnitStyle.NARROW -> when (val platform = TEST_PLATFORM) {
                    is Android if platform.sdk < 34 -> "4 hr. ago"
                    else -> "4h ago"
                }

                RelativeUnitStyle.SHORT -> "4 hr. ago"
                RelativeUnitStyle.LONG -> "4 hours ago"
            }
        }
    }

    context("ifZeroLocalization") {
        context("default case") {
            withTests(
                DurationUnit.SECONDS to "now",
                DurationUnit.MINUTES to "in 0 minutes",
            ) { (minUnit, expected) ->
                val localizer = RelativeDurationLocalizer(
                    options = RelativeDurationOptions(minUnit = minUnit),
                    locale = LOCALE_ENGLISH,
                )
                localizer.localizeAndTestNextTick(500.milliseconds).value shouldBeLocalizedAs expected
            }
        }
        test("custom case") {
            val localizer = RelativeDurationLocalizer(
                options = RelativeDurationOptions(
                    minUnit = DurationUnit.HOURS,
                    ifZeroLocalization = { locale -> "special case for $locale" }
                ),
                locale = LOCALE_ITALIAN,
            )
            localizer.localizeAndTestNextTick(2.minutes) shouldBeLocalizedAs TickingValue(
                value = "special case for it",
                nextTick = 1.hours + 2.minutes,
            )
        }
    }

    context("nextTick") {
        val localizer = RelativeDurationLocalizer(locale = LOCALE_ENGLISH)
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

class RelativeDurationLocalizerTest : FunSpec({
    include(RelativeDurationLocalizerTestFactory)
})

private fun RelativeDurationLocalizer.localizeAndTestNextTick(duration: Duration): TickingValue<String> {
    return localizeAndTestNextTick(
        params = duration,
        advanceBy = Duration::minus, // As time advances, the duration will go backwards
        localize = ::localize,
    )
}
