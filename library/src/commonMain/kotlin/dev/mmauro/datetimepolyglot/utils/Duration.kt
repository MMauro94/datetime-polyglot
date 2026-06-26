package dev.mmauro.datetimepolyglot.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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


/**
 * Returns the amount of time that needs to pass for this duration's [unit] to change, assuming the duration "moves" backwards, i.e. a
 * positive duration means something that will happen in the future, and a negative duration something that has happened in the past.
 *
 * Note: if the duration is positive but smaller than 1 [unit], the value for the unit would remain 0 for both the remaining part of the
 * positive part of the duration and a part of the negative part (see last example).
 *
 * For example:
 * - `-1h20m`, `HOURS` -> `40m`
 * - `-1h20m`, `MINUTES` -> `1m`
 * - `1h20m`, `HOURS` -> `20m1ns`
 * - `1h20m`, `MINUTES` -> `1ns`
 * - `20m`, `HOURS` -> `1h20m`
 */
internal fun Duration.remainderUntilNextUnitBoundary(unit: DurationUnit): Duration {
    val durationNanos = this.absoluteValue.inWholeNanoseconds
    val oneUnit = 1.toDuration(unit)
    val unitNanos = oneUnit.inWholeNanoseconds
    val remainderNs = durationNanos % unitNanos
    return if (isPositive()) {
        if (durationNanos < unitNanos) {
            // This means that the given unit is zero, as the duration is smaller than 1 unit
            // So in order for this unit to change we need to get all the way to zero (this) + wait one full unit length (oneUnit)
            this + oneUnit
        } else {
            remainderNs.nanoseconds + 1.nanoseconds
        }
    } else {
        (unitNanos - remainderNs).nanoseconds
    }
}
