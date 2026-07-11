package dev.mmauro.datetimepolyglot.utils

internal fun <T : Comparable<T>, R : Comparable<R>> OpenEndRange<T>.map(transform: (T) -> R): OpenEndRange<R> {
    return (start.let(transform))..<(endExclusive.let(transform))
}