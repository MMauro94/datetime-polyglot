package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.localize
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.Instant

typealias RelativeInstantOptions = RelativeDurationOptions

/**
 * Localizes an [Instant] relative to another one.
 *
 * This class is a simple convenience over [RelativeDurationLocalizer], simply calculating the diff and forwarding the localization request
 * to it. As this implements [PolyglotReferenceValueLocalizer], this allows to use [localizeAsFlow].
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
 * @see PolyglotReferenceValueLocalizer
 */
class RelativeInstantLocalizer(
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<Instant> {

    private val relativeDurationLocalizer = RelativeDurationLocalizer(options, locale)

    override fun localize(value: Instant, reference: Zoned<Instant>): TickingValue<String> {
        return localize(value, reference.value)
    }

    // As TZ info is not strictly necessary for this, let's also expose a simpler function with a simple Instant as a reference
    fun localize(value: Instant, reference: Instant): TickingValue<String> {
        return relativeDurationLocalizer.localize(value - reference)
    }
}

/**
 * Localizes this [Instant] relatively with respect to [reference], with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeDurationLocalizer
 */
fun Instant.localizeRelative(
    reference: Instant,
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return RelativeInstantLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [Instant] relatively with respect to [clock], with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeDurationLocalizer
 */
fun Instant.localizeRelative(
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): TickingValue<String> {
    return RelativeInstantLocalizer(options, locale).localize(this, clock)
}

/**
 * Localizes this [Instant] relatively with respect to [clock], with a single unit with the given [options] in the given [locale], returning
 * a [Flow].
 *
 * @see RelativeDurationLocalizer
 * @see localizeAsFlow
 */
fun Instant.localizeRelativeAsFlow(
    options: RelativeInstantOptions = RelativeInstantOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): Flow<String> {
    return RelativeInstantLocalizer(options, locale).localizeAsFlow(this, clock)
}