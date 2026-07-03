package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.plus
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.KotlinInstantRange
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.duration
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.kotlinInstant
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlinx.datetime.TimeZone
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Instant

data class WithReferenceFormatParams<T>(val value: T, val reference: Zoned<Instant>) {
    operator fun plus(duration: Duration) = WithReferenceFormatParams(value, reference + duration)
}

fun <T> Arb.Companion.withReferenceFormatParams(
    value: Arb<T>,
    referenceRange: KotlinInstantRange = Instant.DISTANT_PAST..Instant.DISTANT_FUTURE,
) = arbitrary {
    WithReferenceFormatParams(
        value = value.bind(),
        reference = Arb.zonedInstant(referenceRange).bind(),
    )
}

/**
 * Runs tests that validate that the computed [TickingValue.nextTick] is correctly predicting a change in localization.
 *
 * Two similar but distinct fuzzy tests are performed:
 * - one using arbitrary [P] values from the given [arbitraryArb]
 * - one using an arbitrary [P] values from [smallArb], which receives a small [Duration] as input (between -100 and 100 days), to be used
 *   to calculate the params. This duration is meant to be used as a way to create more realistic params to localize.
 *
 * The second case guarantees that more realistic cases (where now and the value to localize are close) are covered.
 */
suspend fun <P> FunSpecContainerScope.nextTickPredictsChangeTest(
    arbitraryArb: Arb<P>,
    smallArb: (Arb<Duration>) -> Arb<P>,
    advanceBy: P.(Duration) -> P,
    localize: (P) -> TickingValue<String>,
) {
    context("correctly predicts date change") {
        // This first test just checks arbitrary values
        // However, these values will likely be very far apart, and wouldn't necessarily test all relevant cases
        test("arbitrary values") {
            checkAll(arbitraryArb) { params ->
                val localized = localize(params)
                testNextTickPredictsChange(params, localized, advanceBy, localize)
            }
        }

        // This test instead computes the value from a small amount of time away from now, ensuring more realistic cases are also covered
        test("small diff value") {
            checkAll(
                smallArb(Arb.duration(-100.days..100.days)),
            ) { params ->
                val localized = localize(params)
                testNextTickPredictsChange(params, localized, advanceBy, localize)
            }
        }
    }
}


/**
 * Version of [nextTickPredictsChangeTest] to test a formatter accepting a [T] and a [Zoned]<[Instant]>.
 */
suspend fun <T> FunSpecContainerScope.nextTickPredictsChangeTest(
    arbitraryArb: Arb<T>,
    smallArb: (Zoned<Instant>) -> Arb<T>,
    localize: (T, reference: Zoned<Instant>) -> TickingValue<String>,
    referenceRange: KotlinInstantRange = Instant.DISTANT_PAST..Instant.DISTANT_FUTURE,
) = nextTickPredictsChangeTest(
    arbitraryArb = Arb.withReferenceFormatParams(arbitraryArb, referenceRange),
    smallArb = { diff ->
        arbitrary {
            val reference = Arb.zonedInstant(referenceRange).bind()
            WithReferenceFormatParams(
                value = smallArb(reference + diff.bind()).bind(),
                reference = reference,
            )
        }
    },
    advanceBy = WithReferenceFormatParams<T>::plus,
    localize = { localize(it.value, it.reference) },
)

private fun <P> testNextTickPredictsChange(
    value: P,
    localized: TickingValue<String>,
    advanceBy: P.(Duration) -> P,
    localize: (P) -> TickingValue<String>,
) {
    if (localized.nextTick == null) {
        withClue("nextTick is null, so localize in the very far future (100 years) should yield same value") {
            localize(value.advanceBy((365 * 100).days)) shouldBe localized
        }
    } else {
        withClue("nextTick is ${localized.nextTick} would be @ ${value.advanceBy(localized.nextTick - 1.nanoseconds)}") {
            withClue("localize at 1 nanosecond before nextTick should yield same relative value") {
                localize(value.advanceBy(localized.nextTick - 1.nanoseconds)) should {
                    it.value shouldBe localized.value
                    it.nextTick shouldBe 1.nanoseconds
                }
            }

            withClue("localize at nextTick should yield different relative value") {
                localize(value.advanceBy(localized.nextTick)) should {
                    it.value shouldNotBe localized.value
                }
            }
        }
    }
}

fun <P> localizeAndTestNextTick(
    params: P,
    advanceBy: P.(Duration) -> P,
    localize: (P) -> TickingValue<String>,
): TickingValue<String> {
    return localize(params).also { localized ->
        testNextTickPredictsChange(
            value = params,
            localized = localized,
            advanceBy = advanceBy,
            localize = localize,
        )
    }
}



fun <T> localizeAndTestNextTick(
    value: T,
    reference: Zoned<Instant>,
    localize: (T, reference: Zoned<Instant>) -> TickingValue<String>,
): TickingValue<String> {
    val params = WithReferenceFormatParams(value, reference)
    return localizeAndTestNextTick(
        params = params,
        advanceBy = WithReferenceFormatParams<T>::plus,
        localize = { localize(it.value, it.reference) },
    )
}
