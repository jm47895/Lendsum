package com.lendsumapp.lendsum.repository

import com.lendsumapp.lendsum.ui.MessagesFragment
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class MessagesRepository @Inject constructor(){

    companion object{
        private val TAG = MessagesRepository::class.simpleName
    }
}