package com.xooloo.r8bug.notifications

import android.content.Context
import androidx.annotation.Keep
import com.xooloo.r8bug.messages.getContent
import com.xooloo.r8bug.model.messages.JsonMessage

@Keep
@Suppress("RedundantSuspendModifier")
class ServerMessageDispatcher(
    private val context: Context
) {
    fun onMessage(msg: JsonMessage) {
        msg.getContent(context) ?: return
    }
}

