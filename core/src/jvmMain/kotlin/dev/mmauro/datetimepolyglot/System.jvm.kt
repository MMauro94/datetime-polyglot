package dev.mmauro.datetimepolyglot

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

public actual val SYSTEM_CLOCK: StateFlow<ClockWrapper> = MutableStateFlow(ClockWrapper(Clock.System))
public actual val SYSTEM_TIMEZONE: StateFlow<TimeZone> = STATIC_TIMEZONE
