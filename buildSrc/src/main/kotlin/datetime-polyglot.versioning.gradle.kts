import dev.mmauro.datetimepolyglot.buildlogic.extensions.DefaultGitVersionExtension

project.extensions.add("gitVersion", DefaultGitVersionExtension(project.providers))
