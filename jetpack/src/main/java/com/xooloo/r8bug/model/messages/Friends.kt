package com.xooloo.r8bug.model.messages

import androidx.room.*
import java.util.*

// ========================================================================
//                             Database Models
// ========================================================================

@Entity(foreignKeys = [
    ForeignKey(entity = User::class, parentColumns = ["uuid"], childColumns = ["uuid"], onDelete = ForeignKey.CASCADE)])
public data class FriendData(
        @PrimaryKey
        val uuid: UUID,
        // ---- Friend Fields
        val name: String,
        val hidden: Boolean,
        val blocked: Boolean,
        val hasAudio: Boolean,
        val hasVideo: Boolean,
        val messagesCount: Int,
        val friendsCount: Int,
        // birthday lookup key
        val birthday: String
)
