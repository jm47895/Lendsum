package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.ChatRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatRoomViewModel @ViewModelInject constructor(
    private val chatRoomRepository: ChatRoomRepository
): ViewModel(){

    fun findUserInRemoteDb(name: String){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.findUserInFirestore(name)
        }
    }

    fun getRemoteDbUserList(): MutableLiveData<List<User>> {

        return chatRoomRepository.getRemoteDbUserList()
    }

    companion object{
        private val TAG = ChatRoomViewModel::class.simpleName
    }
}