package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils

import com.ibm.icu.text.RelativeDateTimeFormatter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

private val CLASS = ClassName("$POLYGLOT_MAIN_PACKAGE.localizers.relative", "RelativeDirection")

enum class RelativeDirection(val icu: RelativeDateTimeFormatter.Direction, val polyglot: MemberName) {
    LAST_2(RelativeDateTimeFormatter.Direction.LAST_2, MemberName(CLASS, "LAST_2")),
    LAST(RelativeDateTimeFormatter.Direction.LAST, MemberName(CLASS, "LAST")),
    THIS(RelativeDateTimeFormatter.Direction.THIS, MemberName(CLASS, "THIS")),
    NEXT(RelativeDateTimeFormatter.Direction.NEXT, MemberName(CLASS, "NEXT")),
    NEXT_2(RelativeDateTimeFormatter.Direction.NEXT_2, MemberName(CLASS, "NEXT_2")),
    ;

    companion object {
        val POLYGLOT_CLASS = CLASS
    }
}
