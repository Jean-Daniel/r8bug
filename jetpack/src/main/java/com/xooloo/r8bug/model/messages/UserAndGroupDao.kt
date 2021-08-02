package com.xooloo.r8bug.model.messages

import androidx.room.Dao
import androidx.room.Query
import java.util.*

@Dao
public abstract class UserDao {
    @Query("SELECT * from UserWithFriendName WHERE uuid = :uuid")
    public abstract suspend fun peek(uuid: UUID): UserWithFriendName?
}
