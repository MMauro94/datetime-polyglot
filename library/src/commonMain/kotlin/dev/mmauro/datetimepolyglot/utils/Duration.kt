package dev.mmauro.datetimepolyglot.utils

import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Returns the part of this duration of the given [unit].
 *
 * For example:
 * - `1h30m`, `MINUTES` -> `30`
 * - `30m`, `HOURS` -> `0`
 * - `-50m44s`, `SECONDS` -> `-44`
 */
@Suppress("MagicNumber")
internal fun Duration.unitPart(unit: DurationUnit): Long {
    return toComponents { days, hours, minutes, seconds, nanoseconds ->
        // IDE complains if we don't put the else, but then the compiler produces a warning if we put it. Let's suppress that warning.
        // RC is that DurationUnit is declared as an expect enum - https://youtrack.jetbrains.com/issue/KT-38750
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (unit) {
            DurationUnit.NANOSECONDS -> (nanoseconds % 1_000).toLong()
            DurationUnit.MICROSECONDS -> ((nanoseconds / 1000) % 1000).toLong()
            DurationUnit.MILLISECONDS -> nanoseconds / 1_000_000L
            DurationUnit.SECONDS -> seconds.toLong()
            DurationUnit.MINUTES -> minutes.toLong()
            DurationUnit.HOURS -> hours.toLong()
            DurationUnit.DAYS -> days
            else -> error("Unknown duration unit: ${this@unitPart}")
        }
    }
}