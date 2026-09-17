package dev.mmauro.datetimepolyglot.buildlogic.tasks

import io.github.z4kn4fein.semver.Version
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class PrintVersionTask : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val version: Property<Version>

    @TaskAction
    fun run() {
        version.orNull?.also {
            println(it)
        }
    }
}