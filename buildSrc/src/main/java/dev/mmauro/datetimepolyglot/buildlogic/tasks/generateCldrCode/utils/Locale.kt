package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils

import com.ibm.icu.util.ULocale
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.buildCodeBlock

private val RFC4647_LOOKUP_FUN = MemberName(POLYGLOT_MAIN_PACKAGE, "rfc4647Lookup")
private val BASE_LOCALE_FUN = MemberName(POLYGLOT_MAIN_PACKAGE, "baseLocale")
private val BCP_47_TAG_FUN = MemberName(POLYGLOT_MAIN_PACKAGE, "bcp47LanguageTag")

fun whenLocale(
    cases: List<Pair<List<ULocale>, CodeBlock>>,
    recursiveCallWithLocale: (CodeBlock) -> CodeBlock,
    ifNull: CodeBlock? = null
): CodeBlock {
    return buildCodeBlock {
        beginControlFlow("return when (locale.%M.%M)", BASE_LOCALE_FUN, BCP_47_TAG_FUN)
        for ((locales, block) in cases) {
            for (locale in locales) {
                addStatement("%S,", locale.toLanguageTag())
            }
            add("-> ")
            add(block)
            addStatement("")
        }
        add("else -> locale.%M().firstNotNullOfOrNull { %L }", RFC4647_LOOKUP_FUN, recursiveCallWithLocale(CodeBlock.of("it")))
        if (ifNull != null) {
            add(" ?: %L", ifNull)
        }
        addStatement("")

        endControlFlow()
    }
}