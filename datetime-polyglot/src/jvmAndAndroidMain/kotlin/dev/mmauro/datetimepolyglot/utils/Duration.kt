package dev.mmauro.datetimepolyglot.utils

import dev.mmauro.datetimepolyglot.MeasureUnit
import kotlin.time.DurationUnit

internal expect fun DurationUnit.toIcuTimeUnit(): MeasureUnit
