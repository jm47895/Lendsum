package com.lendsumapp.lendsum.viewmodel

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.repository.ChatRoomsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomsViewModel @Inject constructor(
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