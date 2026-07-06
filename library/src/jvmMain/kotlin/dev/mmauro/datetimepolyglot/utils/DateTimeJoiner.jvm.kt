package dev.mmauro.datetimepolyglot.utils

import com.ibm.icu.impl.SimpleFormatterImpl
import com.ibm.icu.text.DateTimePatternGenerator
import com.ibm.icu.text.SimpleDateFormat
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.toDateFormatStyle
import java.time.Instant
import java.time.ZoneOffset

internal actual fun joinDateAndTime(
    locale: PlatformLocale,
    style: DateStyle,
    date: String,
    time: String
): String {
    fun String.literal() = "'${replace("'", "''")}'"

    val pattern = DateTimePatternGenerator.getInstance(locale).getDateTimeFormat(style.toDateFormatStyle())
    val rawPattern = SimpleFormatterImpl.formatRawPattern(
        pattern,
        2, // min
        2, // max
        time.literal(),
        date.literal(),
    )
    return SimpleDateFormat(rawPattern, locale).format(Instant.EPOCH.atZone(ZoneOffset.UTC))
}