package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.MessagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessagesViewModel @ViewModelInject constructor(
    private val messagesRepository: MessagesRepository,
    private val firebaseAuth: FirebaseAuth?
): ViewModel(){

    fun getCurrentCachedUser(): LiveData<User>{

        val uid = firebaseAuth?.currentUser?.uid.toString()
        return messagesRepository.getCurrentCachedUser(uid).asLiveData()
    }

    fun getCurrentCachedMessages(chatRoomId: String): LiveData<List<Message>> {
       return messagesRepository.getCurrentMessages(chatRoomId).asLiveData()
    }

    fun findUserInRemoteDb(name: String){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.findUserInFirestore(name)
        }
    }

    fun getRemoteDbUserList(): MutableLiveData<List<User>> {

        return messagesRepository.getRemoteDbUserList()
    }

    fun cacheNewChatRoom(chatRoom: ChatRoom){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.cacheNewChatRoom(chatRoom)
        }
    }

    fun cacheNewMessage(msg: Message){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.cacheNewMsg(msg)
        }
    }

    fun updateLocalCachedChatRoom(chatRoom: ChatRoom){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.updateLocalCachedChatRoom(chatRoom)
        }
    }

    fun addChatRoomIdToRealTimeDb(userIds: List<String>, chatRoomId: String){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.addChatRoomIdToRealTimeDb(userIds, chatRoomId)
        }
    }

    fun addChatRoomObjectToRealTimeDb(chatRoomId: String, chatRoom: ChatRoom){
        messagesRepository.addChatRoomObjectToRealTimeDb(chatRoomId, chatRoom)
    }

    fun addMessageToRealTimeDb(chatRoomId: String, msg: Message){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.addMessageToRealTimeDb(chatRoomId, msg)
        }
    }

    fun updateChatRoomInRealTimeDb(chatRoom: ChatRoom){
        viewModelScope.launch(Dispatchers.IO){
            messagesRepository.updateChatRoomInRealTimeDb(chatRoom)
        }
    }

    fun syncMessagesData(chatId: String){
        viewModelScope.launch(Dispatchers.IO) {
            messagesRepository.syncMessagesData(chatId, viewModelScope)
        }
    }

    companion object{
        private val TAG = MessagesViewModel::class.simpleName
    }
}