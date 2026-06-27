package dev.mmauro.datetimepolyglot.buildlogic.tasks

import com.ibm.icu.impl.SimpleFormatterImpl
import com.ibm.icu.text.DateFormat
import com.ibm.icu.text.DateTimePatternGenerator
import com.ibm.icu.text.SimpleDateFormat
import com.ibm.icu.util.ULocale
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.time.Instant
import java.time.ZoneOffset
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val MAIN_PACKAGE = "dev.mmauro.datetimepolyglot"

private val PLATFORM_LOCALE = ClassName(MAIN_PACKAGE, "PlatformLocale")
private val DATE_STYLE = ClassName("$MAIN_PACKAGE.localizers.absolute", "DateStyle")
private val LOCALE_ENGLISH = MemberName(MAIN_PACKAGE, "LOCALE_ENGLISH")

enum class DateStyle(val icu: Int, val polyglot: MemberName) {
    SHORT(DateFormat.SHORT, MemberName(DATE_STYLE, "SHORT")),
    MEDIUM(DateFormat.MEDIUM, MemberName(DATE_STYLE, "MEDIUM")),
    LONG(DateFormat.LONG, MemberName(DATE_STYLE, "LONG")),
    FULL(DateFormat.FULL, MemberName(DATE_STYLE, "FULL")),
}

open class GenerateCldrCodeTask : DefaultTask() {
    private val generatedRoot = project.layout.buildDirectory.dir("generated/cldr").get().asFile

    init {
        group = "datetimepolyglot"
        description = ""
        outputs.dir(generatedRoot)
    }

    private fun sourceSetRoot(name: String) = generatedRoot.resolve(name)

    @TaskAction
    fun run() {
        generatedRoot.deleteRecursively()
        generatedRoot.mkdirs()

        val commonMain = sourceSetRoot("commonMain")
        dateTimeJoinerFile().writeTo(commonMain)
    }

    // This code needs to be generated because:
    // - Android ICU doesn't expose SimpleFormatterImpl.formatRawPattern
    // - JS Intl doesn't expose anything that can let us find this joiner pattern in CLDR data
    private fun dateTimeJoinerFile(): FileSpec {
        val patternTemplates = ULocale.getAvailableLocales()
            .flatMap { locale -> DateStyle.entries.map { style -> locale to style } }
            .groupBy { (locale, style) -> dateTimeJoinStringTemplate(locale, style) }

        val dateTimeJoinPatternFun = FunSpec.builder("joinDateAndTime")
            .addModifiers(KModifier.INTERNAL)
            .addParameter("locale", PLATFORM_LOCALE)
            .addParameter("style", DATE_STYLE)
            .addParameter("date", String::class)
            .addParameter("time", String::class)
            .returns(String::class)
            .apply {
                beginControlFlow("return when (locale.toString() to style)")
                for ((patternTemplate, locales) in patternTemplates) {
                    for ((locale, styles) in locales) {
                        addStatement("%S to %M,", locale.toString(), styles.polyglot)
                    }
                    addCode("-> ")
                    addCode(patternTemplate)
                }
                addStatement("else -> joinDateAndTime(%M, style, date, time)", LOCALE_ENGLISH) // Fallback to English
                endControlFlow()
            }
            .build()

        return FileSpec.builder("$MAIN_PACKAGE.utils", "DateTimeJoiner")
            .addFunction(dateTimeJoinPatternFun)
            .build()
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

        return CodeBlock.builder()
            .addStatement("%P", template)
            .build()
    }
}