package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import dev.mmauro.datetimepolyglot.utils.remainderUntilNextUnitBoundary
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Localization options for [RelativeDurationLocalizer] and [Duration.localizeRelative]
 *
 * @property minUnit the smallest unit that will be in output. If the [Duration] is smaller, `0 <min-unit>` will be returned (see also the
 * [ifZeroLocalization] parameter)
 * @property maxUnit the biggest unit that will be in output. If the [Duration] has bigger units, the output will contain the whole number
 * of [maxUnit] (e.g. 123 minutes).
 * @property style the style of the duration unit
 * @property ifZeroLocalization string that will be returned as-is in case if the computed unit is zero.
 * Note that this doesn't necessarily mean that the passed [Duration] is itself [Duration.ZERO], but rather that it's smaller than the
 * [minUnit]. If this is null, the default `0 <min-unit>` localized string will be returned in this case.
 */
data class RelativeDurationOptions(
    val minUnit: DurationUnit = DurationUnit.SECONDS,
    val maxUnit: DurationUnit? = null,
    val style: DurationStyle = DurationStyle.WIDE,
    val ifZeroLocalization: (PlatformLocale) -> String? = { null },
) {
    init {
        require(minUnit >= DurationUnit.SECONDS) { "units smaller than seconds are not supported" }
        if (maxUnit != null) {
            require(maxUnit >= minUnit) { "max unit must be grater or equal to min unit" }
        }
    }
}

/**
 * Formats a single [Duration] unit (usually the biggest one) in a relative way.
 *
 * The [localize] function returns a [TickingValue] value indicating when a new [localize] call is required to update the value.
 *
 * For the purposes of [TickingValue.nextTick], this class assumes that the given [Duration] is obtained from a computation that linearly
 * "moves" backwards, i.e. a positive duration means something that will happen in the future, and a negative duration something that has
 * happened in the past.
 * For instance, if given the duration `10 seconds`, this class assumes that in `1.5 seconds` the [Duration] would be `8.5 seconds`.
 *
 * The output string will contain the correct localized words for "in" and "ago".
 * If "ago" and "in" are not required, use [MultiUnitRelativeDurationLocalizer] which can also use a multiple units.
 *
 * Examples:
 * - `10 minutes ago`
 * - `in 1 hour`
 * - `4h ago`
 * - `4 hr. ago`
 * - `in 2 days`
 */
expect class RelativeDurationLocalizer(
    options: RelativeDurationOptions = RelativeDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotValueLocalizer<Duration, TickingValue<String>> {

    override fun localize(value: Duration): TickingValue<String>
}

/**
 * Localizes this [Duration] relatively with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeDurationLocalizer
 */
fun Duration.localizeRelative(
    options: RelativeDurationOptions = RelativeDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return RelativeDurationLocalizer(options, locale).localize(this)
}

internal fun RelativeDurationOptions.detectUnit(value: Duration): DurationUnit {
    return DurationUnit.entries
        .reversed()
        .filter { if (maxUnit != null) it <= maxUnit else true }
        .filter { it >= minUnit }
        .dropWhile { value.toLong(it) == 0L }
        .firstOrNull()
        ?: minUnit
}

internal fun Duration.internalLocalize(
    options: RelativeDurationOptions,
    locale: PlatformLocale,
    localizeBlock: (value: Long, unit: DurationUnit) -> String,
): TickingValue<String> {
    require(this.isFinite()) { "duration must be finite" }

    val unit = options.detectUnit(this)
    val value = toLong(unit)

    val nextTick = remainderUntilNextUnitBoundary(unit)

    if (value == 0L) {
        options.ifZeroLocalization(locale)?.let { return TickingValue(it, nextTick) }
    }
    return TickingValue(localizeBlock(value, unit), nextTick)
}