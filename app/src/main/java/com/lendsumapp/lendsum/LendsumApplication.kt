package com.lendsumapp.lendsum

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LendsumApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            if(BuildConfig.DEBUG){
                DebugAppCheckProviderFactory.getInstance()
            }else{
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }

        )
    }
}