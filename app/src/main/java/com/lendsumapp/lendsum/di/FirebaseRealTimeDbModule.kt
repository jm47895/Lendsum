package com.lendsumapp.lendsum.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object FirebaseRealTimeDbModule {

    @Provides
    @Singleton
    fun provideRealTimeDb(): DatabaseReference {
        return Firebase.database.reference
    }
}