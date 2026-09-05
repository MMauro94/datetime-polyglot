package dev.mmauro.datetimepolyglot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

@OptIn(DelicateCoroutinesApi::class)
public actual val SYSTEM_CLOCK: StateFlow<ClockWrapper> by lazy {
    intentStateFlow(Intent.ACTION_TIME_CHANGED) { ClockWrapper(Clock.System) }
}

@OptIn(DelicateCoroutinesApi::class)
public actual val SYSTEM_TIMEZONE: StateFlow<TimeZone> by lazy {
    intentStateFlow(Intent.ACTION_TIMEZONE_CHANGED, TimeZone::currentSystemDefault)
}

@OptIn(DelicateCoroutinesApi::class)
private fun <T> intentStateFlow(
    action: String,
    valueProvider: () -> T,
): StateFlow<T> {
    return callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == action) {
                    check(!trySend(valueProvider()).isFailure) {
                        "Failed to send update in Channel for action $action"
                    }
                }
            }
        }

        APPLICATION_CONTEXT.registerReceiver(
            receiver,
            IntentFilter(action),
        )

        awaitClose {
            APPLICATION_CONTEXT.unregisterReceiver(receiver)
        }
    }.stateIn(GlobalScope, SharingStarted.Lazily, valueProvider())
}
