package com.xooloo.r8bug.model.util

import androidx.room.TypeConverter
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

/// epoch millis
public typealias Timestamp = Long

public fun Timestamp.toDateTime(): OffsetDateTime {
    return OffsetDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneOffset.systemDefault())
}

public fun Timestamp.toISODateTime(): String {
    return DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(this / 1000))
}

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
public annotation class ISO8061

public class DateTimeAdapter {

    @ToJson
    public fun toExternal(@ISO8061 timestamp: Timestamp): String {
        return timestamp.toISODateTime()
    }

    @FromJson
    @ISO8061
    public fun timestamp(date: String): Timestamp {
        return OffsetDateTime.parse(date).toEpochSecond() * 1000L
    }

    // Nullable (use the Long type instead of long, so Moshi needs both)
    @ToJson
    public fun toNullableExternal(@ISO8061 timestamp: Long?): String? {
        if (timestamp == null)
            return null
        return toExternal(timestamp)
    }

    @FromJson
    @ISO8061
    public fun nullableTimestamp(date: String?): Long? {
        if (date.isNullOrEmpty())
            return null
        return timestamp(date)
    }

    // even in Room, we want to use a format easy to query (especially to be able to query birthdays)
    @ToJson
    @TypeConverter
    public fun toString(value: LocalDate): String = value.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @FromJson
    @TypeConverter
    public fun localeDate(value: String?): LocalDate? = LocalDate.parse(value)

    @ToJson
    public fun toJson(value: LocalDateTime): String =
        value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @FromJson
    public fun localeDateTime(value: String?): LocalDateTime? = LocalDateTime.parse(value)

    // ================================
    // Room optimized representation
    // ================================
    @TypeConverter
    public fun toDatabase(value: OffsetDateTime): Long = value.toEpochSecond() * 1000L

    @TypeConverter
    public fun datetime(value: Long): OffsetDateTime =
        OffsetDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.systemDefault())

    // Local Date Time used by pop-up
    @TypeConverter
    public fun toDatabase(value: LocalDateTime?): Long = value?.toEpochSecond(ZoneOffset.UTC) ?: -1

    @TypeConverter
    public fun localDatetime(value: Long): LocalDateTime? =
        if (value > 0) LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC) else null
}
