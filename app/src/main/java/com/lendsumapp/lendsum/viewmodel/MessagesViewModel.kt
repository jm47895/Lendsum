package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.repository.MessagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessagesViewModel @ViewModelInject constructor(
    private val messagesRepository: MessagesRepository
): ViewModel(){

    fun getCachedChatRooms(){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.getCachedChatRooms()
        }
    }

    fun getChatRooms(): MutableLiveData<List<ChatRoom>> {
        return messagesRepository.getChatRooms()
    }

    companion object{
        private val TAG = MessagesViewModel::class.simpleName
    }
}