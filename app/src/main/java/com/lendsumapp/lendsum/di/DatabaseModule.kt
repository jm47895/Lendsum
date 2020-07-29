package com.lendsumapp.lendsum.di

import android.content.Context
import androidx.room.Room
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.data.persistence.BundleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule{

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
}