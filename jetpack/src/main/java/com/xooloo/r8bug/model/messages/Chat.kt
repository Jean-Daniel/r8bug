package com.xooloo.r8bug.model.messages

import android.content.Context
import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.xooloo.r8bug.model.util.Timestamp
import org.threeten.bp.LocalDate
import java.util.*

public open class BaseChat(
    @PrimaryKey
    @field:Json(name = "id") public val id: Long,
    @field:Json(name = "name") public val name: String?,
    @field:Json(name = "messages_count") public val messageCount: Int,
    @field:Json(name = "messages_unread") @ColumnInfo(defaultValue = "0") public val unread: Int,
    @field:Json(name = "admin") public val admin: UUID?,
    @field:Json(name = "muted") public val isMuted: Boolean,
    @field:Json(name = "private") @ColumnInfo public val isPrivate: Boolean,
    @field:Json(name = "archived") public val isArchived: Boolean,
    @field:Json(name = "active") public val isActive: Boolean
) // did the admin closed this chat ?

// ========================================================================
//                             JSON Models
// ========================================================================

@JsonClass(generateAdapter = true)
public open class JsonChat(
    id: Long,
    name: String?,
    messageCount: Int,
    unread: Int = 0,
    admin: UUID?,
    isMuted: Boolean,
    isPrivate: Boolean,
    isArchived: Boolean,
    isActive: Boolean,
    @field:Json(name = "users") public val users: List<JsonChatUser>? = null
) : BaseChat(id, name, messageCount, unread, admin, isMuted, isPrivate, isArchived, isActive)

@JsonClass(generateAdapter = true)
public class JsonChatWithLastMessage(
    id: Long,
    name: String?,
    messageCount: Int,
    unread: Int = 0,
    admin: UUID?,
    isMuted: Boolean,
    isPrivate: Boolean,
    isArchived: Boolean,
    isActive: Boolean,
    users: List<JsonChatUser>? = null,
    @field:Json(name = "last_message") public val lastMessage: JsonMessage? = null
) : JsonChat(id, name, messageCount, unread, admin, isMuted, isPrivate, isArchived, isActive, users)


@JsonClass(generateAdapter = true)
public class JsonChatUser(
    uuid: UUID, // ro
    username: String, // ro
    firstName: String,
    gender: Gender?,
    birthDate: LocalDate,
    profile: UserProfile,

    @field:Json(name = "active") public val isActive: Boolean
) : User(uuid, username, firstName, gender, birthDate, profile)


// ========================================================================
//                             Database Models
// ========================================================================
@Entity
public class Chat(
    id: Long,
    name: String?,
    messageCount: Int,
    unread: Int, // keeped up to date when receiving new messages
    admin: UUID?,
    isMuted: Boolean,
    isPrivate: Boolean,
    isArchived: Boolean,
    isActive: Boolean,

    // Denormalization:
    // - lastMessageDate: required to sort chats in chat list without having to fetch messages in all chats.
    @ColumnInfo(defaultValue = "0") public val lastMessageDate: Timestamp
) : BaseChat(id, name, messageCount, unread, admin, isMuted, isPrivate, isArchived, isActive) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        if (id != other.id) return false
        if (name != other.name) return false
        if (messageCount != other.messageCount) return false
        if (admin != other.admin) return false
        if (isMuted != other.isMuted) return false
        if (isPrivate != other.isPrivate) return false
        if (isArchived != other.isArchived) return false
        if (isActive != other.isActive) return false
        if (unread != other.unread) return false
        if (lastMessageDate != other.lastMessageDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + messageCount
        result = 31 * result + admin.hashCode()
        result = 31 * result + isMuted.hashCode()
        result = 31 * result + isPrivate.hashCode()
        result = 31 * result + isArchived.hashCode()
        result = 31 * result + isActive.hashCode()
        result = 31 * result + unread
        result = 31 * result + lastMessageDate.hashCode()
        return result
    }
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Chat::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
public class ChatMeta(
    @PrimaryKey public val id: Long, // chat id (but keep using id to be able to use 'JOIN USING')
    // Keep stat of the last get chats call. It is required to reliably update the counter internally.
    // and avoid useless message fetching.
    public val unread: Int, // unread message count returned by the last get chats call
    public val lastMessageId: Long, // id of the last message returned by the last get chats call.
    public val lastReadMessageId: Long = -1, // id of the last message read in the chat.
    // id of the last message fetched for this chat (using pagination API with message ID)
    public val lastFetchedId: Long = -1,
    public val endOfPagingReached: Boolean = false
) // Used by paging to determine if there is older messages on the server or not.

@Entity(
    primaryKeys = ["chatId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = Chat::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("chatId", name = "chatUserCrossRef_chatId_idx"),
        Index("userId", name = "chatUserCrossRef_userId_idx")
    ]
)
public class ChatUserCrossRef(
    public val chatId: Long,
    public val userId: UUID,
    public val isActive: Boolean
)

// View used to workaround limitation of room. There is no way to access extra columns in many-to-many join table.
@DatabaseView("SELECT User.*, chatId, isActive, FriendData.name as friendName, FriendData.hasAudio as hasAudio, FriendData.hasVideo as hasVideo, FriendData.blocked \nFROM ChatUserCrossRef JOIN User ON User.uuid = ChatUserCrossRef.userId\nLEFT JOIN FriendData USING(uuid)")
public data class ChatUser(
    @Embedded val user: User,
    val chatId: Long,
    val isActive: Boolean,
    val friendName: String?,
    val hasAudio: Boolean?,
    val hasVideo: Boolean?,
    val blocked: Boolean?
) {

    val screenName: String
        get() = friendName ?: user.firstName
}

public data class ChatWithUsers(
    @Embedded val chat: Chat,
    @Relation(
        parentColumn = "id",
        entityColumn = "chatId"
    )
    val users: List<ChatUser>
) {

    val id: Long
        get() = chat.id

    // For private chat only
    public fun getUser(self: UUID?): ChatUser? {
        if (!chat.isPrivate || self == null)
            return null
        return users.firstOrNull { it.user.uuid != self }
    }

    // Return any memeber (including self)
    public fun getMember(member: UUID): ChatUser? {
        return users.firstOrNull { it.user.uuid == member }
    }

    // Chat blocked if all participants are blocked
    public fun isBlocked(self: UUID?): Boolean {
        return users.all { it.blocked == true || it.user.uuid == self }
    }

    public fun screenName(context: Context, self: UUID?): String {
        if (chat.name != null)
            return chat.name
        val users = mutableListOf<ChatUser>()
        // Take admin first
        if (chat.admin != self) {
            this.users.firstOrNull { it.user.uuid == chat.admin }?.let {
                users.add(it)
            }
        }
        // sort by UUID (unless getting something better) because we want a stable order.
        val others = this.users.sortedBy { it.user.uuid }
            .filterTo(users) { it.isActive && it.user.uuid != self && it.user.uuid != chat.admin }
        return when (others.size) {
            0 -> "-"// no active user -> no need to display a meaningfull name.
            1 -> others.first().screenName
            2 -> String.format("%s & %s", others[0].screenName, others[1].screenName)
            3 -> String.format(
                "%s, %s & %s",
                others[0].screenName,
                others[1].screenName,
                others[2].screenName
            )
            else -> String.format("%s, %s & %s", others[0].screenName, others[1].screenName, "toto")
        }
    }

}
