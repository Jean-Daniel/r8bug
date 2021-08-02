package com.xooloo.r8bug.model.messages

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.xooloo.r8bug.model.util.ISO8061
import com.xooloo.r8bug.model.util.Timestamp
import java.util.*

public abstract class BaseMessage(
        //Message ID
        @field:Json(name = "id") @PrimaryKey public val id: Long,
        //Sender's user uuid
        @field:Json(name = "uuid") public val sender: UUID,
        //Sender's display name
        @field:Json(name = "first_name") public val senderName: String?,
        // Message body
        @field:Json(name = "text") public val text: String?,
        //Event type
        @field:Json(name = "event") public val event: String?,

        @field:Json(name = "date_sent") @field:ISO8061 public val sentDate: Timestamp,
        // Last modification time of message (in case of reporting)
        @field:Json(name = "date_update") @field:ISO8061 public val updateDate: Timestamp?,
        // Declared read time of the message (if the user is a recipient)
        @field:Json(name = "date_read") @field:ISO8061 public val readDate: Timestamp?,

        // ID of the chat the message belongs to, if any
        @field:Json(name = "chat") public val chat: Long?,
        @field:Json(name = "state") @field:ModelAdapter.MessageState public val state: MessageState,
        @field:Json(name = "reported") public val reported: Boolean,
) {

    public val isEvent: Boolean
        get() = event != null

    internal fun toMessage(self: UUID): Message =
            Message(id, sender, senderName, text, event, sentDate, updateDate, readDate, chat, state, reported, self == sender)
}

// ========================================================================
//                             JSON Models
// ========================================================================
@JsonClass(generateAdapter = true)
public class JsonMessage(
        id: Long,
        sender: UUID,
        senderName: String?,
        text: String?,
        event: String?,
        sentDate: Timestamp,
        updateDate: Timestamp?,
        readDate: Timestamp?,
        chat: Long?,
        state: MessageState,
        reported: Boolean,
        @field:Json(name = "attachments") public val attachments: List<JsonAttachment>?
) : BaseMessage(id, sender, senderName, text, event, sentDate, updateDate, readDate, chat, state, reported)


// ========================================================================
//                             Database Models
// ========================================================================
public typealias MessageState = Int

@Entity(indices = [Index("chat")])
public class Message(
        id: Long,
        sender: UUID,
        senderName: String?,
        text: String?,
        event: String?,
        sentDate: Timestamp,
        updateDate: Timestamp?,
        readDate: Timestamp?,
        chat: Long?,
        state: MessageState,
        reported: Boolean,
        // Database only field to simplify queries
        public val isSelf: Boolean) : BaseMessage(id, sender, senderName, text, event, sentDate, updateDate, readDate, chat, state, reported) {

    public object State {
        public const val Sent: Int = 0
        public const val Distributed: Int = 1
        public const val Read: Int = 2
        public const val ReadAll: Int = 3
        public const val Deleted: Int = 4 // Warning: ordinal hard-coded in VisibleMessage View.

        // Greater than Deleted as query are using "state < x" conditions
        public const val Pending: Int = 0x10001
        public const val Sending: Int = 0x10002
        public const val Error: Int = 0x10003
        public const val Denied: Int = 0x10004
    }

    // true if sent.
    public val isSent: Boolean
        get() = state < State.Pending

    override fun toString(): String {
        return "Message { id: $id }"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false
        if (sender != other.sender) return false
        if (senderName != other.senderName) return false
        if (text != other.text) return false
        if (event != other.event) return false
        if (sentDate != other.sentDate) return false
        if (updateDate != other.updateDate) return false
        if (readDate != other.readDate) return false
        if (chat != other.chat) return false
        if (state != other.state) return false
        if (reported != other.reported) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + (senderName?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (event?.hashCode() ?: 0)
        result = 31 * result + sentDate.hashCode()
        result = 31 * result + (updateDate?.hashCode() ?: 0)
        result = 31 * result + (readDate?.hashCode() ?: 0)
        result = 31 * result + (chat?.hashCode() ?: 0)
        result = 31 * result + state.hashCode()
        result = 31 * result + reported.hashCode()
        return result
    }

}

// Message from non-blocked user and not mark as deleted
// stats < Deleted -> "state < 4"
@DatabaseView("SELECT Message.* \n  FROM Message \n  LEFT JOIN FriendData ON Message.sender == FriendData.uuid \n    WHERE state != ${Message.State.Deleted} AND (FriendData.blocked IS NULL OR NOT FriendData.blocked)")
public data class VisibleMessage(@Embedded val message: Message)

public data class MessageWithAttachments(
        @Embedded val message: Message,
        @Relation(
                parentColumn = "id",
                entityColumn = "messageId")
        val attachments: List<Attachment>) {

    override fun toString(): String {
        return message.toString()
    }
}

public data class MessageWithUser(
        @Embedded val messageWithAttachments: MessageWithAttachments,
        @Relation(
                parentColumn = "sender",
                entityColumn = "uuid")
        val user: UserWithFriendName?) {

    val message: Message
        get() = messageWithAttachments.message

    val attachments: List<Attachment>
        get() = messageWithAttachments.attachments
}


// ========================================================================
//                            Custom Types
// ========================================================================

// WARNING: ordinal is used for database storage. Do not change
internal fun messageStatetoJson(state: MessageState): String {
    return when (state) {
        Message.State.Sent -> "sent"
        Message.State.Distributed -> "received"
        Message.State.Read -> "read"
        Message.State.ReadAll -> "read_all"
        Message.State.Deleted -> "deleted"
        else -> "<invalid>"
    }
}

internal fun messageStateFromJson(value: String): MessageState = when (value) {
    "sent" -> Message.State.Sent
    "received" -> Message.State.Distributed
    "read" -> Message.State.Read
    "read_all" -> Message.State.ReadAll
    "deleted" -> Message.State.Deleted
    // should we really have a default ?
    else -> Message.State.Read
}
