package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.files

import com.ibm.icu.text.DisplayContext
import com.ibm.icu.text.RelativeDateTimeFormatter
import com.ibm.icu.util.ULocale
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.PLATFORM_LOCALE
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.POLYGLOT_MAIN_PACKAGE
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.RelativeDirection
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.whenLocale
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.utils.toAbsoluteUnit
import kotlinx.datetime.DayOfWeek
import java.security.MessageDigest

// This code needs to be generated because:
// - JS Intl doesn't expose anything that localizes days of week with a relative direction
fun relativeDateOfWeekFiles(): FileSpec {
    val localeFunctions = ULocale.getAvailableLocales()
        .groupBy { whenForLocale(it) }
        .mapKeys { localesFunction(it.value, it.key) }

    val localizeRelativeDayOfWeekRootFun = FunSpec.builder("localizeRelativeDayOfWeek")
        .addModifiers(KModifier.INTERNAL)
        .addParameters(parameters())
        .returns(String::class.asClassName().copy(nullable = true))
        .addCode(
            whenLocale(
                cases = localeFunctions.entries.map { (function, locales) ->
                    locales to CodeBlock.builder().add("%N(style, direction, dayOfWeek)", function).build()
                },
                recursiveCallWithLocale = {
                    CodeBlock.of("localizeRelativeDayOfWeek(%L, style, direction, dayOfWeek)", it)
                },
            )
        )
        .build()

    return FileSpec.builder("${POLYGLOT_MAIN_PACKAGE}.utils", "RelativeDateOfWeek")
        .addFunction(localizeRelativeDayOfWeekRootFun)
        .addFunctions(localeFunctions.keys)
        .build()
}

private fun List<ULocale>.hash(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(joinToString().toByteArray())
    return digest.toHexString()
}

private fun parameters() = listOf(
    ParameterSpec.builder("locale", PLATFORM_LOCALE).build(),
    ParameterSpec.builder("style", RelativeUnitStyle.POLYGLOT_CLASS).build(),
    ParameterSpec.builder("direction", RelativeDirection.POLYGLOT_CLASS).build(),
    ParameterSpec.builder("dayOfWeek", DayOfWeek::class).build(),
)

private fun localesFunction(locales: List<ULocale>, codeBlock: CodeBlock): FunSpec {
    return FunSpec.builder("localizeRelativeDayOfWeek_${locales.hash()}")
        .addModifiers(KModifier.PRIVATE)
        .addParameters(parameters().filter { it.name != "locale" })
        .returns(String::class.asClassName().copy(nullable = true))
        .addCode(codeBlock)
        .build()
}

private fun whenForLocale(locale: ULocale): CodeBlock {
    return buildCodeBlock {
        beginControlFlow("return when (style)")
        for (style in RelativeUnitStyle.entries) {
            add("%M -> ", style.polyglot)

            val formatter = RelativeDateTimeFormatter.getInstance(locale, null, style.icu, DisplayContext.CAPITALIZATION_NONE)

            beginControlFlow("when (direction)")
            for (direction in RelativeDirection.entries) {
                add("%M -> ", direction.polyglot)

                add(dayOfWeekWhen(formatter, direction))
            }
            endControlFlow()
        }
        endControlFlow()
    }
}

private fun dayOfWeekWhen(formatter: RelativeDateTimeFormatter, direction: RelativeDirection): CodeBlock {
    val formats = DayOfWeek.entries.associateWith {
        formatter.format(direction.icu, it.toAbsoluteUnit())
    }

    if (formats.values.all { it == null }) {
        return CodeBlock.builder().addStatement("null").build()
    }

    return buildCodeBlock {
        beginControlFlow("when (dayOfWeek)")
        for ((dayOfWeek, formatted) in formats) {
            add("%M -> ", DayOfWeek::class.asClassName().member(dayOfWeek.name))
            if (formatted != null) {
                addStatement("%S", formatted)
            } else {
                addStatement("null")
            }
        }
        endControlFlow()
    }
}