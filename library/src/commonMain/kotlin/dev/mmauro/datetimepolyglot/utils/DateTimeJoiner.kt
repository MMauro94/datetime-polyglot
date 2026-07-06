package dev.mmauro.datetimepolyglot.utils

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle

internal expect fun joinDateAndTime(
    locale: PlatformLocale,
    style: DateStyle,
    date: String,
    time: String,
): String