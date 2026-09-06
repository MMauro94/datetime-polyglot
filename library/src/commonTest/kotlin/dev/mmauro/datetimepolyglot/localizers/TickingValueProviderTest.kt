@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.TickingValueProvider
import dev.mmauro.datetimepolyglot.toFlow
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.engine.coroutines.testScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class TickingValueProviderTest : FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("toFlow").config(coroutineTestScope = true) {
        test("some next ticks and then null") {
            val provider = tickingValueProvider(listOf(1.seconds, 5.minutes, null))
            val flow = provider.toFlow(
                clock = flowOf(ClockWrapper(testScheduler.clock())),
                timeZone = flowOf(TimeZone.UTC),
            )

            flow.test {
                withClue("flow should output second localized value after 1 second") {
                    testCoroutineScheduler.advanceTimeBy(1.seconds)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 2
                    lastItem shouldBe "localized @ 1 UTC"
                }

                withClue("flow should output third localized value after 5 minutes") {
                    testCoroutineScheduler.advanceTimeBy(5.minutes)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 3
                    lastItem shouldBe "localized @ 301 UTC"
                }

                shouldHaveStoppedEmittingValues()
            }
        }
        test("immediate null nextTick") {
            val provider = tickingValueProvider(listOf(null))
            val flow = provider.toFlow(
                clock = flowOf(ClockWrapper(testScheduler.clock())),
                timeZone = flowOf(TimeZone.UTC),
            )

            flow.test {
                shouldHaveStoppedEmittingValues()
            }
        }
        test("never ending next ticks") {
            val flow = tickingValueProvider { 1.seconds }.toFlow(
                clock = flowOf(ClockWrapper(testScheduler.clock())),
                timeZone = flowOf(TimeZone.UTC),
            )

            flow.test {
                withClue("verify flow output after 1 day") {
                    testCoroutineScheduler.advanceTimeBy(1.days)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 86401
                    lastItem shouldBe "localized @ 86400 UTC"
                }
            }
        }
        test("clock update") {
            val provider = tickingValueProvider { null }
            val flow = provider.toFlow(
                clock = flow {
                    emit(ClockWrapper(testScheduler.clock()))

                    delay(1.seconds)
                    val fixedClock = object : Clock {
                        override fun now() = Instant.fromEpochSeconds(1234)
                    }
                    emit(ClockWrapper(fixedClock))

                    delay(5.seconds)
                    emit(ClockWrapper(fixedClock))
                },
                timeZone = flowOf(TimeZone.UTC),
            )

            flow.test {
                withClue("flow should recompute localized value after clock change") {
                    testCoroutineScheduler.advanceTimeBy(1.seconds)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 2
                    lastItem shouldBe "localized @ 1234 UTC"
                }
                withClue("flow should recompute localized value after clock change even if clock is identical") {
                    testCoroutineScheduler.advanceTimeBy(5.seconds)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 3
                    lastItem shouldBe "localized @ 1234 UTC"
                }

                shouldHaveStoppedEmittingValues()
            }
        }
        test("timezone update") {
            val provider = tickingValueProvider { null }
            val flow = provider.toFlow(
                clock = flowOf(ClockWrapper(testScheduler.clock())),
                timeZone = flow {
                    emit(TimeZone.UTC)

                    delay(1.seconds)
                    emit(TimeZone.of("Europe/London"))

                    delay(1.minutes)
                    emit(FixedOffsetTimeZone(UtcOffset(hours = -4)))

                    delay(15.seconds)
                    emit(FixedOffsetTimeZone(UtcOffset(hours = -4)))
                },
            )

            flow.test {
                withClue("flow should recompute localized value after first time zone change") {
                    testCoroutineScheduler.advanceTimeBy(1.seconds)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 2
                    lastItem shouldBe "localized @ 1 Europe/London"
                }
                withClue("flow should recompute localized value after second time zone change") {
                    testCoroutineScheduler.advanceTimeBy(1.minutes)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 3
                    lastItem shouldBe "localized @ 61 -04:00"
                }
                withClue("flow should NOT recompute localized value if new identical timezone is emitted") {
                    testCoroutineScheduler.advanceTimeBy(15.seconds)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 3
                    lastItem shouldBe "localized @ 61 -04:00"
                }
            }
        }
    }
})

private fun tickingValueProvider(nextTicks: List<Duration?>): TickingValueProvider<String> {
    return tickingValueProvider(nextTick = { i -> nextTicks[i] })
}

private fun tickingValueProvider(nextTick: (Int) -> Duration?): TickingValueProvider<String> {
    var count = 0
    return { reference ->
        TickingValue("localized @ ${reference.value.epochSeconds} ${reference.timeZone.id}", nextTick(count++))
    }
}

private fun TestCoroutineScheduler.clock() = object : Clock {
    override fun now(): Instant {
        return Instant.fromEpochMilliseconds(currentTime)
    }
}

data class FlowTestContext(
    var itemsCount: Int,
    var lastItem: String?,
)

context(testScope: TestScope)
private fun Flow<String>.test(test: FlowTestContext.() -> Unit) {
    val context = FlowTestContext(itemsCount = 0, lastItem = null)
    val job = testScope.launch {
        collect {
            context.itemsCount++
            context.lastItem = it
        }
    }

    withClue("flow should immediately output first localized value") {
        testScope.testCoroutineScheduler.runCurrent()
        context.itemsCount shouldBe 1
        context.lastItem shouldBe "localized @ 0 UTC"
    }

    context.test()
    job.cancel()
}

context(testScope: TestScope)
private fun FlowTestContext.shouldHaveStoppedEmittingValues() {
    val oldCount = itemsCount

    withClue("flow should stop outputting values after nextTick is null") {
        testScope.testCoroutineScheduler.advanceTimeBy(365.days)
        testScope.testCoroutineScheduler.runCurrent()
        itemsCount shouldBe oldCount // no new item
    }
}
