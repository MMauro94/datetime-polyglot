package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.withNextTickAtMost
import kotlin.time.Duration
import kotlin.time.Instant

@RequiresOptIn(message = "This API is experimental. It could change or be dropped in the future without notice.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class ExperimentalDynamicLocalizer

/**
 * Special localizer class that allows to pick an arbitrary [PolyglotReferenceValueLocalizer] based on the current reference time.
 *
 * On localization, it goes through each one of [thresholds], stopping at the first one whose reference is in [Case.Threshold.range], and
 * returning its value. If the reference is in none of the thresholds' ranges, [default] is selected instead.
 *
 * Note that it is not recommended to provide thresholds that overlap. However, the class will not stop you from doing that and should work
 * correctly regardless.
 *
 * The next tick is selected with the minimum between the selected case and all thresholds that come before it.
 */
@ExperimentalDynamicLocalizer
public class DynamicLocalizer<T>(
    public val thresholds: List<Case.Threshold<T>>,
    public val default: Case.Default<T>,
) : PolyglotReferenceValueLocalizer<T> {

    public constructor(vararg cases: Case.Threshold<T>, default: Case.Default<T>) : this(cases.toList(), default)

    public sealed interface Case<T> {

        public val localize: (T, Zoned<Instant>) -> TickingValue<String>

        public data class Threshold<T>(
            val range: OpenEndRange<Instant>,
            override val localize: (T, Zoned<Instant>) -> TickingValue<String>,
        ) : Case<T> {

            public constructor(
                range: OpenEndRange<Instant>,
                localizer: PolyglotReferenceValueLocalizer<T>
            ) : this(range, localizer::localize)

            public constructor(
                range: OpenEndRange<Instant>,
                localizer: PolyglotValueLocalizer<T, String>
            ) : this(range, localize = { value, _ -> TickingValue(localizer.localize(value), nextTick = null) })

            public operator fun contains(value: Instant): Boolean = value in range

            internal fun nextTick(reference: Instant): Duration? {
                return if (reference < range.start) {
                    range.start - reference
                } else if (reference < range.endExclusive) {
                    range.endExclusive - reference
                } else {
                    null
                }
            }

            internal companion object {
                internal fun <T : Comparable<T>> computeRangeFromDiff(value: T, diff: IntRange, minus: T.(Int) -> T): OpenEndRange<T> {
                    return (value.minus(diff.last))..<(value.minus(diff.first - 1))
                }
            }
        }

        public data class Default<T>(
            override val localize: (T, Zoned<Instant>) -> TickingValue<String>,
        ) : Case<T> {

            public constructor(
                localizer: PolyglotReferenceValueLocalizer<T>
            ) : this(localizer::localize)

            public constructor(localizer: PolyglotValueLocalizer<T, String>) : this(
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