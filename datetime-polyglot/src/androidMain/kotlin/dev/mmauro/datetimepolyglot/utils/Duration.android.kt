package dev.mmauro.datetimepolyglot.utils

import android.icu.util.TimeUnit
import dev.mmauro.datetimepolyglot.MeasureUnit
import kotlin.time.DurationUnit

internal actual fun DurationUnit.toIcuTimeUnit(): MeasureUnit = when (this) {
    DurationUnit.NANOSECONDS -> TimeUnit.NANOSECOND
    DurationUnit.MICROSECONDS -> TimeUnit.MICROSECOND
    DurationUnit.MILLISECONDS -> TimeUnit.MILLISECOND
    DurationUnit.SECONDS -> TimeUnit.SECOND
    DurationUnit.MINUTES -> TimeUnit.MINUTE
    DurationUnit.HOURS -> TimeUnit.HOUR
    DurationUnit.DAYS -> TimeUnit.DAY
}
