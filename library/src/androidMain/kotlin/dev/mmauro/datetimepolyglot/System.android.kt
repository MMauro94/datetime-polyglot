package dev.mmauro.datetimepolyglot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

@OptIn(DelicateCoroutinesApi::class)
public actual val SYSTEM_CLOCK: Flow<Clock.System> by lazy {
    callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_TIME_CHANGED) {
                    check(!trySend(Clock.System).isFailure) {
                        "Failed to send Clock.System update in Channel"
                    }
                }
            }
        }

        APPLICATION_CONTEXT.registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_TIME_CHANGED),
        )

        send(Clock.System)

        awaitClose {
            APPLICATION_CONTEXT.unregisterReceiver(receiver)
        }
    }.conflate().shareIn(GlobalScope, SharingStarted.Lazily)
}

@OptIn(DelicateCoroutinesApi::class)
public actual val SYSTEM_TIMEZONE: Flow<TimeZone> by lazy {
    callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
                    check(!trySend(TimeZone.currentSystemDefault()).isFailure) {
                        "Failed to send Clock.System update in Channel"
                    }
                }
            }
        }

        APPLICATION_CONTEXT.registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_TIMEZONE_CHANGED),
        )

        send(TimeZone.currentSystemDefault())

        awaitClose {
            APPLICATION_CONTEXT.unregisterReceiver(receiver)
        }
    }.conflate().distinctUntilChanged().shareIn(GlobalScope, SharingStarted.Lazily)
}
