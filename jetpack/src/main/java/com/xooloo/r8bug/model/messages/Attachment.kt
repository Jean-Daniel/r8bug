package com.xooloo.r8bug.model.messages

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
public class JsonAttachment(
    @field:Json(name = "id") public val id: Long,
    @field:Json(name = "file_path") public val fileUrl: String,
    @field:Json(name = "file_name") public val fileName: String,
    @field:Json(name = "file_size") public val fileSize: Long,
    @field:Json(name = "file_type") public val fileType: String,
    @field:Json(name = "file_duration") public val fileDuration: Int? = 0)

// ========================================================================
//                             Database Models
// ========================================================================
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Message::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [
        Index("messageId", name = "attachment_messageId_idx")]
)
public data class Attachment(
    @PrimaryKey val id: Long,
    val fileUrl: String,
    val fileName: String,
    val fileSize: Long,
    val fileType: String,
    val fileDuration: Int, // seconds
    val messageId: Long = 0
)
