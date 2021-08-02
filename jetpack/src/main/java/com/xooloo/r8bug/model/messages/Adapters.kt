package com.xooloo.r8bug.model.messages

import androidx.room.TypeConverter
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import com.xooloo.r8bug.model.ext.UUID
import com.xooloo.r8bug.model.ext.toBytes
import java.util.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
public annotation class ByteString

public open class UUIDAdapter {
    @ToJson
    public fun toJson(value: UUID): String = value.toString()

    @FromJson
    public fun uuidFromJson(value: String): UUID = UUID.fromString(value)

    @TypeConverter
    public fun toDatabase(value: UUID?): ByteArray? = value?.toBytes()

    @TypeConverter
    public fun uuidFromDatabse(value: ByteArray?): UUID? = value?.let(::UUID)
}

public class ModelAdapter : UUIDAdapter() {

    @Retention(AnnotationRetention.RUNTIME)
    @JsonQualifier
    public annotation class MessageState

    // ===================== User Types
    @ToJson
    @TypeConverter
    public fun toJson(value: Gender?): String = value.toJson()

    @FromJson
    @TypeConverter
    public fun gender(value: String?): Gender? = genderFromJson(value)

    @ToJson
    public fun byteArrayToString(@ByteString value: ByteArray?): String? {
        return value?.decodeToString()
    }

    @FromJson
    @ByteString
    public fun byteString(str: String?): ByteArray? {
        return str?.toByteArray()
    }

    // ===================== Account Types
    @ToJson
    @TypeConverter
    public fun toJson(value: ParentRole?): String = value.toJson()

    @FromJson
    @TypeConverter
    public fun parentRole(value: String): ParentRole? = parentRoleFromJson(value)

    @ToJson
    @TypeConverter
    public fun toJson(value: IMType): String = value.toJson()

    @FromJson
    @TypeConverter
    public fun imType(value: String): IMType = imTypeFromJson(value)

    @ToJson
    @TypeConverter
    public fun toJson(value: OSFamily): String = value.toJson()

    @FromJson
    @TypeConverter
    public fun osFamily(value: String): OSFamily = osFamilyFromJson(value)

    // ===================== Message Types
    @ToJson
    public fun toJson(@MessageState value: Int): String = messageStatetoJson(value)

    @FromJson
    @MessageState
    public fun state(value: String): Int = messageStateFromJson(value)
}
