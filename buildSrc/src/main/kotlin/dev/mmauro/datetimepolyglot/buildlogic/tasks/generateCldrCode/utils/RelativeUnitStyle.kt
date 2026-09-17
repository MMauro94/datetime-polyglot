package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils

import com.ibm.icu.text.RelativeDateTimeFormatter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

private val CLASS = ClassName("$POLYGLOT_MAIN_PACKAGE.styles", "RelativeUnitStyle")

enum class RelativeUnitStyle(val icu: RelativeDateTimeFormatter.Style, val polyglot: MemberName) {
    NARROW(RelativeDateTimeFormatter.Style.NARROW, MemberName(CLASS, "NARROW")),
    SHORT(RelativeDateTimeFormatter.Style.SHORT, MemberName(CLASS, "SHORT")),
    LONG(RelativeDateTimeFormatter.Style.LONG, MemberName(CLASS, "LONG")),
    ;

    companion object {
        val POLYGLOT_CLASS = CLASS
    }
}
