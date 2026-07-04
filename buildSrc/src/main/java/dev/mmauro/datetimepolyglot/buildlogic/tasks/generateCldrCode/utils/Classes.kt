package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

const val POLYGLOT_MAIN_PACKAGE = "dev.mmauro.datetimepolyglot"
val PLATFORM_LOCALE = ClassName(POLYGLOT_MAIN_PACKAGE, "PlatformLocale")
val LOCALE_ENGLISH = MemberName(POLYGLOT_MAIN_PACKAGE, "LOCALE_ENGLISH")
