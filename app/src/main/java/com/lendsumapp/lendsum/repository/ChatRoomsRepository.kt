package com.lendsumapp.lendsum.repository

import androidx.lifecycle.MutableLiveData
import com.lendsumapp.lendsum.data.DataSyncManager
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRoomsRepository @Inject constructor(
    private val cacheDatabase: LendsumDatabase,
    private val dataSyncManager: DataSyncManager
){

    fun getCachedChatRooms(): Flow<List<ChatRoom>>{
        return cacheDatabase.getChatRoomDao().getAllChatRooms()
    }

    fun registerChatRoomSyncListener(userId: String){
        dataSyncManager.registerChatRoomSyncListener(userId)
    }

    fun unregisterChatRoomSyncListener(userId: String){
        dataSyncManager.unregisterChatRoomSyncListener(userId)
    }

    fun getNumberOfChatIdsFromRealtimeDb(): MutableLiveData<MutableList<String>> {
        return dataSyncManager.getNumberOfChatIdsFromRealtimeDb()
    }

    fun syncChatRoomData(chatIdList: MutableList<String>){
        dataSyncManager.syncChatRoomData(chatIdList)
    }

    companion object{
        private val TAG = ChatRoomsRepository::class.simpleName
    }
}