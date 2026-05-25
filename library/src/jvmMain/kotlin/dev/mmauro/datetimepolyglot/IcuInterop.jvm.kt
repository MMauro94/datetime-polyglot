package dev.mmauro.datetimepolyglot

import com.ibm.icu.text.DateFormat
import com.ibm.icu.text.DateTimePatternGenerator
import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.UResourceBundle

internal actual typealias SimpleDateFormat = SimpleDateFormat

internal actual typealias DateFormat = DateFormat

internal actual fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale) =
    DateFormat.getInstanceForSkeleton(skeleton, locale)
