package dev.mmauro.datetimepolyglot

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

public actual val SYSTEM_CLOCK: Flow<Clock.System> = flowOf(Clock.System)
public actual val SYSTEM_TIMEZONE: Flow<TimeZone> = flow { emit(TimeZone.currentSystemDefault()) }