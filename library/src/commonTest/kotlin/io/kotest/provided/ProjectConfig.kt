package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode

class ProjectConfig: AbstractProjectConfig() {
    override val duplicateTestNameMode = DuplicateTestNameMode.Error
}