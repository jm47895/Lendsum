package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.repository.ChatRoomsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatRoomsViewModel @ViewModelInject constructor(
    private val chatRoomsRepository: ChatRoomsRepository
): ViewModel(){

    fun getCachedChatRooms(): LiveData<List<ChatRoom>>{
        return chatRoomsRepository.getCachedChatRooms().asLiveData()
    }

    companion object{
        private val TAG = ChatRoomsViewModel::class.simpleName
    }
}