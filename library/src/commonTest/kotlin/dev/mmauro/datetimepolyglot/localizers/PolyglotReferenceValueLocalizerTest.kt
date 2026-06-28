@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mokkery.answering.calls
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.engine.coroutines.testScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

private const val LOCALIZED_VALUE = "value"

class PolyglotReferenceValueLocalizerTest : FunSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    context("localizeAsFlow").config(coroutineTestScope = true) {
        test("some next ticks and then null") {
            val localizer = mockLocalizer(listOf(1.seconds, 5.minutes, null))
            val flow = localizer.localizeAsFlow(LOCALIZED_VALUE, testScheduler.clock())

            flow.test {
                withClue("flow should output second localized value after 1 second") {
                    testCoroutineScheduler.advanceTimeBy(1.seconds)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 2
                    lastItem shouldBe "localized @ 1"
                }

                withClue("flow should output third localized value after 5 minutes") {
                    testCoroutineScheduler.advanceTimeBy(5.minutes)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 3
                    lastItem shouldBe "localized @ 301"
                }

                shouldHaveStoppedEmittingValues()
            }
        }
        test("immediate null nextTick") {
            val localizer = mockLocalizer(listOf(null))
            val flow = localizer.localizeAsFlow(LOCALIZED_VALUE, testScheduler.clock())

            flow.test {
                shouldHaveStoppedEmittingValues()
            }
        }
        test("never ending next ticks") {
            val localizer = mockLocalizer { 1.seconds }
            val flow = localizer.localizeAsFlow(LOCALIZED_VALUE, testScheduler.clock())

            flow.test {
                withClue("verify flow output after 1 day") {
                    testCoroutineScheduler.advanceTimeBy(1.days)
                    testCoroutineScheduler.runCurrent()
                    itemsCount shouldBe 86401
                    lastItem shouldBe "localized @ 86400"
                }
            }
        }
    }
})

private fun mockLocalizer(nextTicks: List<Duration?>): PolyglotReferenceValueLocalizer<String> {
    return mockLocalizer(nextTick = { i -> nextTicks[i] })
}

private fun mockLocalizer(nextTick: (Int) -> Duration?): PolyglotReferenceValueLocalizer<String> {
    return mock {
        var count = 0
        every { localize(LOCALIZED_VALUE, any()) } calls {
            val reference = it.arg<Zoned<Instant>>(1)
            TickingValue("localized @ ${reference.value.epochSeconds}", nextTick(count++))
        }
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
        context.lastItem shouldBe "localized @ 0"
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