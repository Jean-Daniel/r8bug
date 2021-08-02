package com.xooloo.r8bug.model.messages

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


// ========================================================================
//                           Data Access Object
// ========================================================================

@Dao
public abstract class ChatDao {
    @Transaction
    @Query("SELECT * FROM Chat WHERE NOT isArchived ORDER BY lastMessageDate DESC, name ASC")
    public abstract fun getVisibleChats(): Flow<List<ChatWithUsers>>

    @Transaction
    @Query("SELECT * FROM Chat WHERE id = :chatId")
    public abstract suspend fun peekChat(chatId: Long): ChatWithUsers?

    @Transaction
    @Query("SELECT * FROM VisibleMessage WHERE chat = :chatId ORDER BY sentDate DESC, id DESC LIMIT 1")
    public abstract fun getLastMessageWithUser(chatId: Long): Flow<MessageWithUser?>
}
