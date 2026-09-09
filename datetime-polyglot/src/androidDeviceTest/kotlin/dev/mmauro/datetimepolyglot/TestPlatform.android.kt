package dev.mmauro.datetimepolyglot

import android.os.Build

actual val TEST_PLATFORM: TestPlatform = TestPlatform.Android(Build.VERSION.SDK_INT)
