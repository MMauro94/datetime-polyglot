package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeDurationLocalizer
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import dev.mmauro.datetimepolyglot.utils.unitPart
import kotlin.collections.ifEmpty
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Localization options for [DurationLocalizer] and [Duration.localize]
 *
 * @property minUnit the smallest unit that will be in output. If the [Duration] is smaller, `0 <min-unit>` will be returned (see also the
 * [ifZeroLocalization] parameter)
 * @property maxUnits maximum number of units that will be retuned. If the [Duration] spans more than [maxUnits] units (e.g. 3h 1m 3s), only
 * the biggest [maxUnits] will be returned.
 * @property omitZeros whether to omit middle units that are zero. If a unit is omitted, the next non-zero smaller one will be returned
 * (e.g. omitZero=false, maxUnits=2, output=3h 0m; omitZero=true, maxUnits=2, output=3h 3s)
 * @property style the style of duration units, Also applies to the style of the list (in case there is more than one unit to show)
 * @property ifZeroLocalization string that will be returned as-is in case if all allowable units to display are zero. Note that this
 * doesn't necessarily mean that the passed [Duration] is itself [Duration.ZERO], but rather that it's smaller than the [minUnit]. If this
 * is null, the default `0 <min-unit>` localized string will be returned in this case.
 */
public data class DurationOptions(
    val minUnit: DurationUnit = DurationUnit.SECONDS,
    val maxUnits: Int = 2,
    val omitZeros: Boolean = true,
    val style: DurationStyle = DurationStyle.WIDE,
    val ifZeroLocalization: (PlatformLocale) -> String? = { null },
) : PolyglotLocalizerOptions<DurationLocalizer> {
    init {
        require(maxUnits > 0) { "maxUnits must be greater than zero" }
    }

    override fun localizer(locale: PlatformLocale): DurationLocalizer = DurationLocalizer(this, locale)
}

/**
 * Localizer for [Duration].
 *
 * This is meant to localize "static" duration, that don't change over time (e.g. a movie length).
 *
 * For localizing durations that change over time see [TickingDurationLocalizer].
 * For localizing durations relatively, see [RelativeDurationLocalizer].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [Duration.localize] for one-off localizations.
 *
 * Examples:
 * - `1h 5m`
 * - `1 hr, 5 min`
 * - `1 hour, 5 minutes`
 */
public expect class DurationLocalizer(
    options: DurationOptions = DurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotValueLocalizer<Duration, String> {
    internal val options: DurationOptions

    override fun localize(value: Duration): String
}

/**
 * Localizes this [Duration] with the given [options] in the given [locale].
 *
 * @see DurationLocalizer
 */
public fun Duration.localize(
    options: DurationOptions = DurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): String {
    return DurationLocalizer(options, locale).localize(this)
}

internal fun DurationOptions.detectUnits(value: Duration) = DurationUnit.entries
    .reversed()
    .filter { it >= minUnit }
    .dropWhile { value.unitPart(it) == 0L }
    .take(maxUnits)
    .ifEmpty { listOf(minUnit) }
    .map { value.unitPart(it) to it }


internal fun Duration.internalLocalize(
    options: DurationOptions,
    locale: PlatformLocale,
    localizeBlock: (filteredUnits: List<Pair<Long, DurationUnit>>) -> String,
): String {
    require(this.isFinite()) { "duration must be finite" }

    val units = options.detectUnits(this)

    val filteredUnits = units.filter { (value) -> !options.omitZeros || value != 0L }
        .ifEmpty { listOf(units.last()) }

    if (filteredUnits.all { (value) -> value == 0L }) {
        options.ifZeroLocalization(locale)?.let { return it }
    }
    return localizeBlock(filteredUnits)
}