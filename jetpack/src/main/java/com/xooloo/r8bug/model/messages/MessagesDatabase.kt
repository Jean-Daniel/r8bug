package com.xooloo.r8bug.model.messages

import androidx.room.Database
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xooloo.r8bug.model.ext.DatabaseFactory
import com.xooloo.r8bug.model.util.DateTimeAdapter

// Important: this database must only contains data recoverable by fetching server.
// This make possible to drop the whole database on data inconsistency and simply rebuild it from server data.
@Database(
    version = 2,
    entities = [
        User::class, FriendData::class,
        Message::class, Attachment::class,
        Chat::class, ChatUserCrossRef::class, ChatMeta::class,
    ],
    views = [ChatUser::class, UserWithFriendName::class, VisibleMessage::class]
)
@RewriteQueriesToDropUnusedColumns
@TypeConverters(DateTimeAdapter::class, ModelAdapter::class)
public abstract class MessagesDatabase : RoomDatabase() {

    public abstract val users: UserDao
    public abstract val chats: ChatDao

    public companion object : DatabaseFactory<MessagesDatabase>
}
