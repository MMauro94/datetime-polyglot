package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils

import com.ibm.icu.text.DateFormat
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

private val CLASS = ClassName("$POLYGLOT_MAIN_PACKAGE.localizers.absolute", "DateStyle")

enum class DateStyle(val icu: Int, val polyglot: MemberName) {
    SHORT(DateFormat.SHORT, MemberName(CLASS, "SHORT")),
    MEDIUM(DateFormat.MEDIUM, MemberName(CLASS, "MEDIUM")),
    LONG(DateFormat.LONG, MemberName(CLASS, "LONG")),
    FULL(DateFormat.FULL, MemberName(CLASS, "FULL")),
    ;

    companion object {
        val POLYGLOT_CLASS = CLASS
    }
}
