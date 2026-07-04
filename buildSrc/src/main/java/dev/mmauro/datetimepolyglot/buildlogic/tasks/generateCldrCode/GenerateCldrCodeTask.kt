package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode

import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.files.dateTimeJoinerFile
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.files.relativeDateOfWeekFiles
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

open class GenerateCldrCodeTask : DefaultTask() {
    private val output = project.layout.buildDirectory.dir("generated/cldr").get().asFile

    @OutputDirectory
    val androidMainOutput = output.resolve("androidMain")
    @OutputDirectory
    val webMainOutput = output.resolve("webMain")

    init {
        group = "datetimepolyglot"
        description = "Generates code that has hardcoded CLDR data. Needed for stuff that is not exposed by all localization backends."
        outputs.dir(output)
    }

    @TaskAction
    fun run() {
        output.deleteRecursively()
        output.mkdirs()

        dateTimeJoinerFile().writeTo(androidMainOutput)
        dateTimeJoinerFile().writeTo(webMainOutput)

        relativeDateOfWeekFiles().writeTo(webMainOutput)
    }
}