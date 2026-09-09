package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceDateTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.localizeNow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Localization options for [RelativeInstantLocalizer], [Instant.localizeRelative], and [Instant.localizeRelativeAsFlow].
 */
public data class RelativeInstantOptions(
    val relativeDurationOptions: RelativeDurationOptions = RelativeDurationOptions(),
) : PolyglotLocalizerOptions<RelativeInstantLocalizer> {
    override fun localizer(locale: PlatformLocale): RelativeInstantLocalizer = RelativeInstantLocalizer(this, locale)
}

/**
 * Localizes an [Instant] relative to another one.
 *
 * This class is a simple convenience over [RelativeDurationLocalizer], simply calculating the diff and forwarding the localization request
 * to it. As this implements [PolyglotReferenceDateTimeLocalizer], this allows to use [localizeAsFlow].
 *
 * For this reason, the [options] required here are identical to [RelativeDurationLocalizer].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [Instant.localizeRelative] or [Instant.localizeRelativeAsFlow] for one-off localizations.
 *
 * Examples:
 * - `10 minutes ago`
 * - `in 1 hour`
 * - `4h ago`
 * - `4 hr. ago`
 * - `in 2 days`
 *
 * @see PolyglotReferenceDateTimeLocalizer
 */
public class RelativeInstantLocalizer(
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceDateTimeLocalizer<Instant> {

    private val relativeDurationLocalizer = RelativeDurationLocalizer(
        options = options.relativeDurationOptions,
        locale = locale,
    )

    override fun localize(value: Instant, reference: Zoned<Instant>): TickingValue<String> {
        return localize(value, reference.value)
    }

    // As TZ info is not strictly necessary for this, let's also expose a simpler function with a simple Instant as a reference
    public fun localize(value: Instant, reference: Instant): TickingValue<String> {
        return relativeDurationLocalizer.localize(value - reference)
    }
}

/**
 * Localizes this [Instant] relatively with respect to [reference], with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeDurationLocalizer
 */
public fun Instant.localizeRelative(
    reference: Instant,
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return RelativeInstantLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [Instant] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale].
 *
 * @see RelativeDurationLocalizer
 */
public fun Instant.localizeRelativeNow(
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<String> {
    return RelativeInstantLocalizer(options, locale).localizeNow(this, clock, timeZone)
}

/**
 * Localizes this [Instant] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale], returning a [Flow].
 *
 * @see RelativeDurationLocalizer
 * @see localizeAsFlow
 */
public fun Instant.localizeRelativeAsFlow(
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Flow<ClockWrapper> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
): Flow<String> {
    return RelativeInstantLocalizer(options, locale).localizeAsFlow(this, clock, timeZone)
}
