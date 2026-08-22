package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale

/**
 * Interface that all options classes implement.
 */
public interface PolyglotLocalizerOptions<L : PolyglotLocalizer> {

    /**
     * Returns a new instance of the [PolyglotLocalizer] of type [L] that this options belongs to.
     */
    public fun localizer(locale: PlatformLocale = getDefaultLocale()): L
}
