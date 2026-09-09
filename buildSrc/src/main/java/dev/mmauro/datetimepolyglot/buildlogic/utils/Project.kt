package dev.mmauro.datetimepolyglot.buildlogic.utils

import org.gradle.api.Project

fun Project.isMain(): Boolean {
    return name == "datetime-polyglot"
}
