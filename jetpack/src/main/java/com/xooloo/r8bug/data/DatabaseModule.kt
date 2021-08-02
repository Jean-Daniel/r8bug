package com.xooloo.r8bug.data

import android.content.Context
import com.xooloo.r8bug.model.ext.openSafely
import com.xooloo.r8bug.model.messages.MessagesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun messagesDb(@ApplicationContext context: Context): MessagesDatabase {
        return MessagesDatabase.openSafely(context, "messages.db")
    }

}
