package dev.mmauro.datetimepolyglot

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri

@Suppress("ktlint:standard:property-naming")
internal lateinit var APPLICATION_CONTEXT: Context

public class DatetimePolyglotProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        APPLICATION_CONTEXT = checkNotNull(context) { "null context in DatetimePolyglotProvider" }.applicationContext
        return false
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?,
    ): Int = 0

    override fun getType(uri: Uri): Nothing? = null

    override fun insert(uri: Uri, values: ContentValues?): Nothing? = null

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?,
    ): Nothing? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?,
    ): Int = 0
}
