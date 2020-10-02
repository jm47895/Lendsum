package com.lendsumapp.lendsum.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object FirebaseFirestoreModule {

    @Provides
    @Singleton
    fun provideFirestoreDb(): FirebaseFirestore{
        return Firebase.firestore
    }
}