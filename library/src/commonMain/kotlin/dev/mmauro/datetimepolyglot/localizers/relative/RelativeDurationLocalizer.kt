package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.TickingDurationLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.localizeTicking
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.utils.remainderUntilNextUnitBoundary
import kotlin.time.Duration
import kotlin.time.DurationUnit

private typealias IfZeroLocalization = RelativeDurationLocalizer.ZeroLocalizationContext.(PlatformLocale) -> String?

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
 * [minUnit]. If this returns null, the default `0 <min-unit>` localized string will be used. By default, this localizes the string "now" if
 * the min unit is seconds, returns `null` otherwise.
 */
data class RelativeDurationOptions(
    val minUnit: DurationUnit = DurationUnit.SECONDS,
    val maxUnit: DurationUnit? = null,
    val style: RelativeUnitStyle = RelativeUnitStyle.LONG,
    val ifZeroLocalization: IfZeroLocalization = {
        if (minUnit == DurationUnit.SECONDS) {
            localizeNow()
        } else {
            null
        }
    },
) {
    init {
        require(minUnit >= DurationUnit.SECONDS) { "units smaller than seconds are not supported" }
        if (maxUnit != null) {
            require(maxUnit >= minUnit) { "max unit must be grater or equal to min unit" }
        }
    }
}

/**
 * Localizes a single [Duration] unit (usually the biggest one) in a relative way.
 *
 * The [localize] function returns a [TickingValue] value indicating when a new [localize] call is required to update the value.
 *
 * For the purposes of [TickingValue.nextTick], this class assumes that the given [Duration] is obtained from a computation that linearly
 * "moves" backwards, i.e. a positive duration means something that will happen in the future, and a negative duration something that has
 * happened in the past.
 * For instance, if given the duration `10 seconds`, this class assumes that in `1.5 seconds` the [Duration] would be `8.5 seconds`.
 *
 * The output string will contain the correct localized words for "in" and "ago".
 * If you just need the "next tick" behavior but do not want the relative localization, you can use [TickingDurationLocalizer] instead.
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [Duration.localizeRelative] for one-off localizations.
 *
 * Examples:
 * - `10 minutes ago`
 * - `in 1 hour`
 * - `4h ago`
 * - `4 hr. ago`
 * - `in 2 days`
 */
class RelativeDurationLocalizer(
    private val options: RelativeDurationOptions = RelativeDurationOptions(),
    private val locale: PlatformLocale = getDefaultLocale(),
) : PolyglotValueLocalizer<Duration, TickingValue<String>> {

    interface ZeroLocalizationContext {
        fun localizeNow(): String?
    }

    private val relativeUnitLocalizer = RelativeUnitLocalizer(
        style = options.style,
        locale = locale,
    )

    override fun localize(value: Duration): TickingValue<String> {
        require(value.isFinite()) { "duration must be finite" }

        val unit = options.detectUnit(value)
        val unitValue = value.toLong(unit)

        val nextTick = value.remainderUntilNextUnitBoundary(unit)

        if (unitValue == 0L) {
            val zeroLocalizationContext = object : ZeroLocalizationContext {
                override fun localizeNow() = relativeUnitLocalizer.localizeNow()
            }
            val ifZeroLocalization = options.ifZeroLocalization
            zeroLocalizationContext.ifZeroLocalization(locale)?.let { return TickingValue(it, nextTick) }
        }
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        val relativeUnit = when (unit) {
            // IDE complains if we don't put the else, but then the compiler produces a warning if we put it. Let's suppress that warning.
            // RC is that DurationUnit is declared as an expect enum - https://youtrack.jetbrains.com/issue/KT-38750
            DurationUnit.NANOSECONDS -> error("nanosecond unit not supported")
            DurationUnit.MICROSECONDS -> error("microsecond unit not supported")
            DurationUnit.MILLISECONDS -> error("millisecond unit not supported")
            DurationUnit.SECONDS -> RelativeUnit.DateTimeComponent.SECOND
            DurationUnit.MINUTES -> RelativeUnit.DateTimeComponent.MINUTE
            DurationUnit.HOURS -> RelativeUnit.DateTimeComponent.HOUR
            DurationUnit.DAYS -> RelativeUnit.DateTimeComponent.DAY
            else -> error("Unknown duration unit: $unit")
        }
        return TickingValue(relativeUnitLocalizer.localizeNumeric(unitValue.toDouble(), relativeUnit), nextTick)
    }
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
