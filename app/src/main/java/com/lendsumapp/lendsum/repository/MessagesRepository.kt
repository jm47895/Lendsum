package com.lendsumapp.lendsum.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_IS_PROFILE_PUBLIC_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USERNAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_COLLECTION_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesRepository @Inject constructor(
    private val firestoreDb: FirebaseFirestore,
    private val lendsumDatabase: LendsumDatabase,
    private val realTimeDb: DatabaseReference
){
    private val userList: MutableLiveData<List<User>> = MutableLiveData()

    suspend fun getCurrentCachedUser(userId: String): User{
        return lendsumDatabase.getUserDao().getUser(userId)
    }

    fun getCurrentMessages(chatRoomId: String): Flow<List<Message>> {
        return lendsumDatabase.getChatMessageDao().getChatRoomMessages(chatRoomId)
    }

    fun findUserInFirestore(username: String){
        firestoreDb.collection(FIRESTORE_USER_COLLECTION_PATH).whereEqualTo(FIRESTORE_IS_PROFILE_PUBLIC_KEY, true).orderBy(
            FIRESTORE_USERNAME_KEY).startAt( "@$username").endAt("$username\uf8ff").limit(3).get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    userList.postValue(task.result!!.toObjects(User::class.java))
                    Log.d(TAG, "Name lookup success: " + task.result?.toObjects(User::class.java))
                }else{
                    userList.postValue(emptyList())
                    Log.d(TAG, "Name lookup failed: " + task.exception)
                }
            }
    }

    private suspend fun updateMsgInLocalCache(msg: Message){
        lendsumDatabase.getChatMessageDao().updateChatMessage(msg)
    }

    suspend fun cacheNewChatRoom(chatRoom: ChatRoom){
        lendsumDatabase.getChatRoomDao().insertChatRoom(chatRoom)
    }

    suspend fun cacheNewMsg(msg: Message){
        lendsumDatabase.getChatMessageDao().insertChatMessage(msg)
    }

    suspend fun updateLocalCachedChatRoom(chatRoom: ChatRoom){
        lendsumDatabase.getChatRoomDao().updateChatRoom(chatRoom)
    }

    fun getRemoteDbUserList(): MutableLiveData<List<User>>{
        return userList
    }

    fun addChatRoomObjectToRealTimeDb(chatRoomId: String, chatRoom: ChatRoom){
        realTimeDb.child("chat_rooms").child(chatRoomId).push().setValue(chatRoom).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.d(TAG, "Send chat room to realtime db success")
            }else{
                Log.d(TAG, "Send chat room to realtime db failed: " + task.exception.toString())
            }
        }
    }

    fun addChatRoomIdToRealTimeDb(userIds: List<String>, chatRoomId: String){

        for(userId in userIds) {
            realTimeDb.child("users").child(userId).push().setValue(chatRoomId)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Users sent to realtime db")
                    } else {
                        Log.d(TAG, task.exception.toString())
                    }
                }
        }

    }

    fun addMessageToRealTimeDb(chatRoomId: String, msg: Message){
        realTimeDb.child("messages").child(chatRoomId).push().setValue(msg).addOnCompleteListener { task->
            if(task.isSuccessful){
                Log.d(TAG, "Message sent to realtime db")
                msg.sentToRemoteDb = true
                GlobalScope.launch(Dispatchers.IO) {
                    updateMsgInLocalCache(msg)
                }
            }else{
                Log.d(TAG, "Message failed to send to real time db: " + task.exception.toString())
            }
        }
    }

    fun updateChatRoomInRealTimeDb(chatRoom: ChatRoom){
        realTimeDb.child("chat_rooms").child(chatRoom.chatRoomId).setValue(chatRoom).addOnCompleteListener { task->
            if (task.isSuccessful){
                Log.d(TAG, "Chat room updated in real time db success")
            }else{
                Log.d(TAG, "Chat room updated in real time db failed")
            }
        }
    }

    companion object{
        private val TAG = MessagesRepository::class.simpleName
    }
}