package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.ChatRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRoomViewModel @ViewModelInject constructor(
    private val chatRoomRepository: ChatRoomRepository
): ViewModel(){

    private val currentUser: MutableLiveData<User> = MutableLiveData()
    private val currentListOfMessages: MutableLiveData<LiveData<List<Message>>> = MutableLiveData()

    fun getCurrentCachedUser(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
           currentUser.postValue(chatRoomRepository.getCurrentCachedUser(userId))
        }
    }

    fun getCurrentCachedMessages(chatRoomId: String): LiveData<List<Message>>{
       return chatRoomRepository.getCurrentMessages(chatRoomId)
    }

    fun getUser(): MutableLiveData<User>{
        return currentUser
    }

    fun getCurrentMessages(): MutableLiveData<LiveData<List<Message>>> {
        return currentListOfMessages
    }

    fun findUserInRemoteDb(name: String){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.findUserInFirestore(name)
        }
    }

    fun getRemoteDbUserList(): MutableLiveData<List<User>> {

        return chatRoomRepository.getRemoteDbUserList()
    }

    fun cacheNewChatRoom(chatRoom: ChatRoom){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.cacheNewChatRoom(chatRoom)
        }
    }

    fun cacheNewMessage(msg: Message){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.cacheNewMsg(msg)
        }
    }

    fun updateLocalCachedChatRoom(chatRoom: ChatRoom){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.updateLocalCachedChatRoom(chatRoom)
        }
    }

    fun addChatRoomIdToRealTimeDb(userIds: List<String>, chatRoomId: String){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.addChatRoomIdToRealTimeDb(userIds, chatRoomId)
        }
    }

    fun addChatRoomObjectToRealTimeDb(chatRoomId: String, chatRoom: ChatRoom){
        chatRoomRepository.addChatRoomObjectToRealTimeDb(chatRoomId, chatRoom)
    }

    fun addMessageToRealTimeDb(chatRoomId: String, msg: Message){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.addMessageToRealTimeDb(chatRoomId, msg)
        }
    }

    fun updateChatRoomInRealTimeDb(chatRoom: ChatRoom){
        viewModelScope.launch(Dispatchers.IO){
            chatRoomRepository.updateChatRoomInRealTimeDb(chatRoom)
        }
    }

    companion object{
        private val TAG = ChatRoomViewModel::class.simpleName
    }
}