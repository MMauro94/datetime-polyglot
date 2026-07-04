package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.files

import com.ibm.icu.impl.SimpleFormatterImpl
import com.ibm.icu.text.DateTimePatternGenerator
import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.ULocale
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.buildCodeBlock
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.DateStyle
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.PLATFORM_LOCALE
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.POLYGLOT_MAIN_PACKAGE
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.whenLocale
import java.time.Instant
import java.time.ZoneOffset
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// This code needs to be generated because:
// - Android ICU doesn't expose SimpleFormatterImpl.formatRawPattern
// - JS Intl doesn't expose anything that can let us find this joiner pattern in CLDR data
fun dateTimeJoinerFile(): FileSpec {
    val patternTemplates = ULocale.getAvailableLocales()
        .groupBy { whenForLocale(it) }

    val dateTimeJoinPatternFun = FunSpec.builder("joinDateAndTime")
        .addModifiers(KModifier.INTERNAL)
        .addParameter("locale", PLATFORM_LOCALE)
        .addParameter("style", DateStyle.POLYGLOT_CLASS)
        .addParameter("date", String::class)
        .addParameter("time", String::class)
        .returns(String::class)
        .addCode(
            whenLocale(
                cases = patternTemplates.map {
                    it.value to it.key
                },
                recursiveCallWithLocale = {
                    CodeBlock.of("joinDateAndTime(%L, style, date, time)", it)
                },
                // Worst case, fallback to English
                ifNull = CodeBlock.of("joinDateAndTime(%M, style, date, time)", LOCALE_ENGLISH)
            )
        )
        .build()

    return FileSpec.builder("${POLYGLOT_MAIN_PACKAGE}.utils", "DateTimeJoiner")
        .addFunction(dateTimeJoinPatternFun)
        .build()
}

private fun whenForLocale(locale: ULocale): CodeBlock {
    return buildCodeBlock {
        beginControlFlow("return when (style)")
        for (style in DateStyle.entries) {
            addStatement("%M -> %L", style.polyglot, dateTimeJoinStringTemplate(locale, style))
        }
        endControlFlow()
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun dateTimeJoinStringTemplate(locale: ULocale, style: DateStyle): CodeBlock {
    fun String.toLiteral() = "'${replace("'", "''")}'"

    val pattern = DateTimePatternGenerator.getInstance(locale).getDateTimeFormat(style.icu)

    val timeUuid = Uuid.random().toString()
    val dateUuid = Uuid.random().toString()
    val rawPattern = SimpleFormatterImpl.formatRawPattern(
        pattern,
        2, // min
        2, // max
        timeUuid.toLiteral(),
        dateUuid.toLiteral(),
    )
    val formatted = SimpleDateFormat(rawPattern, locale).format(Instant.EPOCH.atZone(ZoneOffset.UTC))

    val template = formatted
        .replace(timeUuid, $$"${time}")
        .replace(dateUuid, $$"${date}")

    return CodeBlock.of("%P", template)
}