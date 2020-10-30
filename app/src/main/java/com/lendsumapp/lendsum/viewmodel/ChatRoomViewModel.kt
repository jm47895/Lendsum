package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.ChatRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatRoomViewModel @ViewModelInject constructor(
    private val chatRoomRepository: ChatRoomRepository
): ViewModel(){

    private val currentUser: MutableLiveData<User> = MutableLiveData()
    private val currentListOfMessages: MutableLiveData<List<Message>> = MutableLiveData()

    fun getCurrentCachedUser(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
           currentUser.postValue(chatRoomRepository.getCurrentCachedUser(userId))
        }
    }

    fun getCurrentCachedMessages(chatRoomId: String){
        viewModelScope.launch(Dispatchers.IO) {
            currentListOfMessages.postValue(chatRoomRepository.getCurrentMessages(chatRoomId))
        }
    }

    fun getUser(): MutableLiveData<User>{
        return currentUser
    }

    fun getCurrentMessages(): MutableLiveData<List<Message>>{
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

    fun updateExistingCachedChatRoom(chatRoom: ChatRoom){
        viewModelScope.launch(Dispatchers.IO) {
            chatRoomRepository.updateExistingCachedChatRoom(chatRoom)
        }
    }

    /*fun addChatroomUserToRealTimeDb(userIds: List<String>, chatRoomId: String){
        chatRoomRepository.addChatroomsToRealTimeDb(userIds, chatRoomId)
    }

    fun addMessagesToRealTimeDb(chatRoomId: String, listOfMessages: List<Message>){
        chatRoomRepository.addMessagesToRealTimeDb(chatRoomId, listOfMessages)
    }

    fun addParticipantsToRealTimeDb(chatRoomId: String, listOfUser: List<User>){
        chatRoomRepository.addParticipantsToRealTimeDb(chatRoomId, listOfUser)
    }*/


    companion object{
        private val TAG = ChatRoomViewModel::class.simpleName
    }
}