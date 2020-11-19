package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.repository.ChatRoomsRepository
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

    fun registerRealtimeChatIdListener(){
        viewModelScope.launch(Dispatchers.IO) {
            val uid = firebaseAuth.currentUser?.uid.toString()
            chatRoomsRepository.registerRealtimeChatIdListener(uid)
        }
    }

    fun getRealtimeChatIds(): MutableLiveData<MutableList<String>> {
        return chatRoomsRepository.getRealtimeChatIds()
    }

    fun syncChatRoomList(listOfChatIds: List<String>){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomsRepository.syncChatRoomList(listOfChatIds, viewModelScope)
        }
    }

    companion object{
        private val TAG = ChatRoomsViewModel::class.simpleName
    }
}