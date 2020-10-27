package com.lendsumapp.lendsum.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.IS_PROFILE_PUBLIC_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.USERNAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.USER_COLLECTION_PATH
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject


@ActivityScoped
class ChatRoomRepository @Inject constructor(
    private val firestoreDb: FirebaseFirestore,
    private val cacheDatabase: LendsumDatabase
){
    private val userList: MutableLiveData<List<User>> = MutableLiveData()

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
        cacheDatabase.getChatRoomDao().insertChatRoom(chatRoom)
    }

    suspend fun updateExistingCachedChatRoom(chatRoom: ChatRoom){
        cacheDatabase.getChatRoomDao().updateChatRoom(chatRoom)
    }

    fun getRemoteDbUserList(): MutableLiveData<List<User>>{
        return userList
    }

    companion object{
        private val TAG = ChatRoomRepository::class.simpleName
    }
}