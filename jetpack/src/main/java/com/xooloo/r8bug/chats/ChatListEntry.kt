package com.xooloo.r8bug.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import com.xooloo.r8bug.data.ChatRepository
import com.xooloo.r8bug.databinding.ChatsChatBinding
import com.xooloo.r8bug.messages.getContent
import com.xooloo.r8bug.model.messages.ChatWithUsers
import com.xooloo.r8bug.util.ListEntry
import com.xooloo.r8bug.util.ScopedViewBindingHolder
import kotlinx.coroutines.flow.collectLatest


class ChatListEntry(
    val value: ChatWithUsers,
    private val messages: ChatRepository
) : ListEntry() {

    companion object {
        fun onCreateViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            scope: LifecycleCoroutineScope
        ): ScopedViewBindingHolder<*> {
            return ScopedViewBindingHolder(ChatsChatBinding.inflate(inflater, parent, false), scope)
        }
    }

    override fun hasSameContents(other: ListEntry): Boolean {
        if (other !is ChatListEntry)
            return false

        return other.value == value
    }


    override val type: Int
        get() = 1

    override val identifier: Long
        get() = value.chat.id


    fun onBind(
        holder: ScopedViewBindingHolder<*>
    ) {
        val binding = holder.binding as ChatsChatBinding

        holder.launch("last-message") {
            messages.getLastMessage(value.chat).collectLatest {
                binding.message.text = it?.messageWithAttachments?.getContent(holder.context)
            }
        }
    }

}
