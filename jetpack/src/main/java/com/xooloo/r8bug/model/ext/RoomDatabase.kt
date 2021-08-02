package com.xooloo.r8bug.model.ext

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import timber.log.Timber

public interface DatabaseFactory<T : RoomDatabase> {

    public fun build(builder: RoomDatabase.Builder<T>): T {
        return builder.build()
    }
}

public inline fun <reified T : RoomDatabase> DatabaseFactory<T>.inMemory(context: Context): T {
    return build(Room.inMemoryDatabaseBuilder(context, T::class.java))
}

@PublishedApi
internal fun <T : RoomDatabase> DatabaseFactory<T>.openSafely(context: Context, filename: String? = null, isRecoverable: Boolean, cls: Class<T>): T {
    val builder = if (filename == null) {
        Room.inMemoryDatabaseBuilder(context, cls)
    } else {
        Room.databaseBuilder(context, cls, filename)
    }

    val db = build(builder)

    // safe to scrap (popups are not required)
    try {
        // database schema check
        db.openHelper.writableDatabase
    } catch (t: Exception) {
        if (!isRecoverable) {
            // report the error in release mode
            Timber.e(t, "database: $filename")
        } else if (t !is IllegalStateException) {
            // illegal state is expected when missing migration, but other errors must be reported.
            Timber.e(t, "database: $filename")
        }
        // probably database integrity error -> reset database
        context.deleteDatabase(filename)
    }
    return db
}

public inline fun <reified T : RoomDatabase> DatabaseFactory<T>.openSafely(context: Context, filename: String? = null, isRecoverable: Boolean = false): T {
    return openSafely(context, filename, isRecoverable, T::class.java)
}