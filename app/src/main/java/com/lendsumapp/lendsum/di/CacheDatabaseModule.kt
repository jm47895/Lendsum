package com.lendsumapp.lendsum.di

import android.content.Context
import androidx.room.Room
import com.lendsumapp.lendsum.data.persistence.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CacheDatabaseModule{

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): LendsumDatabase {
        return Room.databaseBuilder(
            appContext,
            LendsumDatabase::class.java,
            "lendsum.db"
        ).build()
    }

    @Provides
    fun provideBundleDao(database: LendsumDatabase) : BundleDao{
        return database.getBundleDao()
    }

    @Provides
    fun provideUserDao(database: LendsumDatabase) : UserDao{
        return database.getUserDao()
    }

    @Provides
    fun provideChatRoomDao(database: LendsumDatabase) : ChatRoomDao{
        return database.getChatRoomDao()
    }

    @Provides
    fun provideChatMessageDao(database: LendsumDatabase) : ChatMessageDao{
        return database.getChatMessageDao()
    }
}