package com.lendsumapp.lendsum.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseAuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
}