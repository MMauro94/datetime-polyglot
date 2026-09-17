package dev.mmauro.datetimepolyglot.buildlogic

import dev.mmauro.datetimepolyglot.buildlogic.extensions.DefaultGitInfoExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class GitInfoPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val gitInfo = DefaultGitInfoExtension(project.providers)

        project.extensions.add("gitInfo", gitInfo)
    }
}
