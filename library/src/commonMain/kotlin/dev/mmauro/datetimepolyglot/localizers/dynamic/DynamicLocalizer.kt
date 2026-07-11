package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.withNextTickAtMost
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Internal only-class (for now) that allows to pick a localizer based on some arbitrary thresholds.
 */
internal class DynamicLocalizer<T>(
    val thresholds: List<Case.Threshold<T>>,
    val default: Case.Default<T>,
) : PolyglotReferenceValueLocalizer<T> {

    constructor(vararg cases: Case.Threshold<T>, default: Case.Default<T>) : this(cases.toList(), default)

    sealed interface Case<T> {

        val localize: (T, Zoned<Instant>) -> TickingValue<String>

        data class Threshold<T>(
            val range: OpenEndRange<Instant>,
            override val localize: (T, Zoned<Instant>) -> TickingValue<String>,
        ) : Case<T> {

            constructor(
                range: OpenEndRange<Instant>,
                localizer: PolyglotReferenceValueLocalizer<T>
            ) : this(range, localizer::localize)

            operator fun contains(value: Instant) = value in range

            fun nextTick(reference: Instant): Duration? {
                return if (reference < range.start) {
                    range.start - reference
                } else if (reference < range.endExclusive) {
                    range.endExclusive - reference
                } else {
                    null
                }
            }

            companion object {
                internal fun <T : Comparable<T>> computeRangeFromDiff(value: T, diff: IntRange, minus: T.(Int) -> T): OpenEndRange<T> {
                    return (value.minus(diff.last))..<(value.minus(diff.first - 1))
                }
            }
        }

        data class Default<T>(
            override val localize: (T, Zoned<Instant>) -> TickingValue<String>,
        ) : Case<T> {
            constructor(localizer: PolyglotValueLocalizer<T, String>) : this(
                localize = { value, _ -> TickingValue(localizer.localize(value), nextTick = null) },
            )
        }
    }

    override fun localize(value: T, reference: Zoned<Instant>): TickingValue<String> {
        // Select the first case that is within the threshold, if any
        val threshold = thresholds.withIndex().firstOrNull { reference.value in it.value }

        // Detect which case has the min next tick
        // Here we don't consider any threshold after the current one because, even if their next tick was lower, the current threshold
        // would keep being valid because it comes first in the list (this can only happen if thresholds have overlaps)
        val nextTickAtMost = thresholds
            .subList(0, if (threshold != null) threshold.index + 1 else thresholds.size)
            .mapNotNull { it.nextTick(reference.value) }
            .minOrNull()

        val case = threshold?.value ?: default
        return case.localize(value, reference).withNextTickAtMost(nextTickAtMost)
    }
}