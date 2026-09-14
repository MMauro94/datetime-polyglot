package dev.mmauro.datetimepolyglot.compose.localizers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import dev.mmauro.datetimepolyglot.compose.utils.toPlatformLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions

/**
 * Remembers the localizer from the given [options].
 *
 * @param OPTIONS the type of the options class
 * @param LOCALIZER the type of [PolyglotLocalizer] that [OPTIONS] builds via [PolyglotLocalizerOptions.localizer]
 * @param options the options to use to create the localizer. Every new instance of this class triggers a recomputation of the localizer
 * @return the remembered [LOCALIZER]
 */
@Composable
public fun <OPTIONS : PolyglotLocalizerOptions<LOCALIZER>, LOCALIZER : PolyglotLocalizer> rememberLocalizer(options: OPTIONS): LOCALIZER {
    val locale = LocalLocale.current
    return remember(options, locale) { options.localizer(locale.toPlatformLocale()) }
}

/**
 * Remembers the localizer computed from the given [options], passed to [localizerFactory] and returned by it.
 * @param OPTIONS the type of the options class
 * @param OPTIONS_LOCALIZER the type of [PolyglotLocalizer] that [OPTIONS] builds via [PolyglotLocalizerOptions.localizer]
 * @param LOCALIZER the type of localizer returned by [localizerFactory]
 * @param options the options to use to create the localizer. Every new instance of this class triggers a recomputation of the localizer
 * @param keys any key changes triggers a full recomputation
 * @param localizerFactory lambda that gets as its own parameter the localizers returned by [options], and that derives a new localizer
 * from it
 */
@Composable
@Suppress("ktlint:standard:max-line-length")
public fun <OPTIONS : PolyglotLocalizerOptions<OPTIONS_LOCALIZER>, OPTIONS_LOCALIZER : PolyglotLocalizer, LOCALIZER : PolyglotLocalizer> rememberLocalizer(
    options: OPTIONS,
    vararg keys: Any?,
    localizerFactory: (OPTIONS_LOCALIZER) -> LOCALIZER,
): LOCALIZER {
    val optionsLocalizer = rememberLocalizer(options)
    return remember(optionsLocalizer, *keys) { localizerFactory(optionsLocalizer) }
}
