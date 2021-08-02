package com.xooloo.r8bug.messages

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.xooloo.r8bug.model.ext.fromBrokenJson
import com.xooloo.r8bug.model.messages.BaseMessage
import com.xooloo.r8bug.model.messages.MessageWithAttachments
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import java.util.*

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface Components {
    val moshi: Moshi
}

// Helpers for notifications
val BaseMessage.isChatEvent: Boolean
    get() = event?.startsWith("chat-") == true

fun MessageWithAttachments.getContent(
    context: Context,
): CharSequence? {
    return message.getContent(context)
}

fun BaseMessage.getContent(
    context: Context,
): CharSequence? {
    val components = EntryPointAccessors.fromApplication(context, Components::class.java)

    return kotlin.runCatching {
        getContent(components)
    }.onFailure {
        Timber.e(it)
    }.getOrNull()
}

private fun BaseMessage.getContent(
    components: Components
): CharSequence? {
    if (isChatEvent)
        return onChatEvent(components)

    return text
}


// ------------------------------------------
//  Chat Events
// ------------------------------------------
@JsonClass(generateAdapter = true)
internal class ChatJoinEventBody(
    @field:Json(name = "added-by") val user: UUID,
    @field:Json(name = "added-by_first_name") val firstName: String? = null
)

private fun BaseMessage.onChatEvent(
    components: Components
): CharSequence? {
    return when (event) {
        "chat-join" -> {
            components.moshi.fromBrokenJson<ChatJoinEventBody>("").toString()
        }
        else -> null
    }

}
