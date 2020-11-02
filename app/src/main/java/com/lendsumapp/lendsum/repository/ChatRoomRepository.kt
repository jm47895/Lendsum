package com.lendsumapp.lendsum.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.IS_PROFILE_PUBLIC_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.USERNAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.USER_COLLECTION_PATH
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class ChatRoomRepository @Inject constructor(
    private val firestoreDb: FirebaseFirestore,
    private val lendsumDatabase: LendsumDatabase,
    private val realTimeDb: DatabaseReference
){
    private val userList: MutableLiveData<List<User>> = MutableLiveData()

    suspend fun getCurrentCachedUser(userId: String): User{
        return lendsumDatabase.getUserDao().getUser(userId)
    }

    suspend fun getCurrentMessages(chatRoomId: String): List<Message>{
        return lendsumDatabase.getChatMessageDao().getChatRoomMessages(chatRoomId)
    }

    fun findUserInFirestore(username: String){
        firestoreDb.collection(USER_COLLECTION_PATH).whereEqualTo(IS_PROFILE_PUBLIC_KEY, true).orderBy(
            USERNAME_KEY).startAt( "@$username").endAt("$username\uf8ff").limit(3).get()
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

    suspend fun cacheNewChatRoom(chatRoom: ChatRoom){
        lendsumDatabase.getChatRoomDao().insertChatRoom(chatRoom)
    }

    suspend fun cacheNewMsg(msg: Message){
        lendsumDatabase.getChatMessageDao().insertChatMessage(msg)
    }

    suspend fun updateExistingCachedChatRoom(chatRoom: ChatRoom){
        lendsumDatabase.getChatRoomDao().updateChatRoom(chatRoom)
    }

    fun getRemoteDbUserList(): MutableLiveData<List<User>>{
        return userList
    }

    /*fun addChatroomsToRealTimeDb(userIds: List<String>, chatRoomId: String){

        for(userId in userIds) {
            realTimeDb.child("usr").child(userId).push().setValue(chatRoomId)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Users sent to realtime db")
                    } else {
                        Log.d(TAG, task.exception.toString())
                    }
                }
        }

    }

    fun addMessagesToRealTimeDb(chatRoomId: String, listOfMessages: List<Message>){
        realTimeDb.child("msg").child(chatRoomId).setValue(listOfMessages).addOnCompleteListener { task->
            if(task.isSuccessful){
                Log.d(TAG, "Messages sent to realtime db")
            }else{
                Log.d(TAG, task.exception.toString())
            }
        }
    }

    fun addParticipantsToRealTimeDb(chatRoomId: String, listOfUser: List<User>){
        realTimeDb.child("ptp").child(chatRoomId).setValue(listOfUser)
    }*/

    companion object{
        private val TAG = ChatRoomRepository::class.simpleName
    }
}