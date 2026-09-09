package dev.mmauro.datetimepolyglot.styles

import js.intl.EraFormat
import js.intl.long
import js.intl.narrow
import js.intl.short

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#era)
 */
internal fun EraStyle.toEraFormat(): EraFormat = when (this) {
    EraStyle.NARROW -> EraFormat.narrow
    EraStyle.ABBREVIATED -> EraFormat.short
    EraStyle.WIDE -> EraFormat.long
}
