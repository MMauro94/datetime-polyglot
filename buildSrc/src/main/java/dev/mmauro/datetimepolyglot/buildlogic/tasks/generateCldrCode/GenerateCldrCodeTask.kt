package dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode

import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.files.dateTimeJoinerFile
import dev.mmauro.datetimepolyglot.buildlogic.tasks.generateCldrCode.files.relativeDateOfWeekFiles
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateCldrCodeTask : DefaultTask() {
    @OutputDirectory
    val androidMainOutput = sourceSetDirectory("androidMain")

    @OutputDirectory
    val webMainOutput = sourceSetDirectory("webMain")

    init {
        group = "datetimepolyglot"
        description = "Generates code that has hardcoded CLDR data. Needed for stuff that is not exposed by all localization backends."
        outputs.dir(androidMainOutput)
        outputs.dir(webMainOutput)
    }

    @TaskAction
    fun run() {
        outputs.files.forEach {
            it.deleteRecursively()
            it.mkdirs()
        }

        dateTimeJoinerFile().writeTo(androidMainOutput)
        dateTimeJoinerFile().writeTo(webMainOutput)

        relativeDateOfWeekFiles().writeTo(webMainOutput)
    }

    private fun sourceSetDirectory(name: String): File {
        return project.layout.buildDirectory.dir("generated/cldr/$name").get().asFile
    }
}