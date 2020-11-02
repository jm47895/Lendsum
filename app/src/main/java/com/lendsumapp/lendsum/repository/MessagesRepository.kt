package com.lendsumapp.lendsum.repository

import androidx.lifecycle.MutableLiveData
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.ui.MessagesFragment
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class MessagesRepository @Inject constructor(
    private val cacheDatabase: LendsumDatabase
){

    private val chatRooms : MutableLiveData<List<ChatRoom>> = MutableLiveData()

    suspend fun getCachedChatRooms(){
        chatRooms.postValue(cacheDatabase.getChatRoomDao().getAllChatRooms())
    }

    fun getChatRooms(): MutableLiveData<List<ChatRoom>> {
        return chatRooms
    }

    companion object{
        private val TAG = MessagesRepository::class.simpleName
    }
}