package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.repository.ChatRoomsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatRoomsViewModel @ViewModelInject constructor(
    private val chatRoomsRepository: ChatRoomsRepository
): ViewModel(){

    fun getCachedChatRooms(){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomsRepository.getCachedChatRooms()
        }
    }

    fun getChatRooms(): MutableLiveData<List<ChatRoom>> {
        return chatRoomsRepository.getChatRooms()
    }

    companion object{
        private val TAG = ChatRoomsViewModel::class.simpleName
    }
}