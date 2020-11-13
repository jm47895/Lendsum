package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.repository.ChatRoomsRepository
import com.lendsumapp.lendsum.util.GlobalConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatRoomsViewModel @ViewModelInject constructor(
    private val chatRoomsRepository: ChatRoomsRepository,
    private val firebaseAuth: FirebaseAuth
): ViewModel(){

    val uid = firebaseAuth.currentUser?.uid.toString()

    fun getCachedChatRooms(): LiveData<List<ChatRoom>>{
        return chatRoomsRepository.getCachedChatRooms().asLiveData()
    }

    fun registerChatRoomSyncListener(){
        chatRoomsRepository.registerChatRoomSyncListener(uid)
    }

    fun unregisterChatRoomSyncListener(){
        chatRoomsRepository.unregisterChatRoomSyncListener(uid)
    }

    fun getNumberOfChatIdsFromRealtimeDb(): MutableLiveData<MutableList<String>> {
        return chatRoomsRepository.getNumberOfChatIdsFromRealtimeDb()
    }

    fun syncChatRoomData(chatId: String){
        viewModelScope.launch(Dispatchers.IO){
            chatRoomsRepository.syncChatRoomData(chatId)
        }
    }

    companion object{
        private val TAG = ChatRoomsViewModel::class.simpleName
    }
}