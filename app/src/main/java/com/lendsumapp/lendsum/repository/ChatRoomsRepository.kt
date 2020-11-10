package com.lendsumapp.lendsum.repository

import androidx.lifecycle.MutableLiveData
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRoomsRepository @Inject constructor(
    private val cacheDatabase: LendsumDatabase
){

    fun getCachedChatRooms(): Flow<List<ChatRoom>>{
        return cacheDatabase.getChatRoomDao().getAllChatRooms()
    }

    companion object{
        private val TAG = ChatRoomsRepository::class.simpleName
    }
}