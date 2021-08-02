package com.xooloo.r8bug.data

import com.xooloo.r8bug.model.messages.Chat
import com.xooloo.r8bug.model.messages.ChatWithUsers
import com.xooloo.r8bug.model.messages.MessageWithUser
import com.xooloo.r8bug.model.messages.MessagesDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(private val db: MessagesDatabase) {

    fun chats(): Flow<List<ChatWithUsers>> {
        // FIXME: to properly display Chats, we need up-to-date friends and messages
        // Note: friends are fetched when trying to lookup birthdays, so it usually works.
        return db.chats.getVisibleChats()
    }

    // Utility to get last message in chat list.
    fun getLastMessage(chat: Chat): Flow<MessageWithUser?> {
        // Assuming the last message is always up to date (as it is provided by the get chats API)
        return db.chats.getLastMessageWithUser(chat.id)
    }

}
