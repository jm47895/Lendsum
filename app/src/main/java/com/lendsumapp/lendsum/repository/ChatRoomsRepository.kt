package com.lendsumapp.lendsum.repository

import androidx.lifecycle.MutableLiveData
import com.lendsumapp.lendsum.data.DataSyncManager
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRoomsRepository @Inject constructor(
    private val cacheDatabase: LendsumDatabase,
    private val dataSyncManager: DataSyncManager
){

    fun getCachedChatRooms(): Flow<List<ChatRoom>>{
        return cacheDatabase.getChatRoomDao().getAllChatRooms()
    }

    fun registerRealtimeChatIdListener(uid: String){
        return dataSyncManager.registerRealtimeChatIdListener(uid)
    }

    fun getRealtimeChatIds(): MutableLiveData<MutableList<String>> {
        return dataSyncManager.getRealtimeChatIds()
    }

    fun syncChatRoomList(listOfChatIds: List<String>, scope: CoroutineScope){
        dataSyncManager.syncChatRoomList(listOfChatIds, scope)
    }

    companion object{
        private val TAG = ChatRoomsRepository::class.simpleName
    }
}