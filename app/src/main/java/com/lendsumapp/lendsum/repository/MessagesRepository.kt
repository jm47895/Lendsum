package com.lendsumapp.lendsum.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.lendsumapp.lendsum.data.DataSyncManager
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_IS_PROFILE_PUBLIC_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USERNAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USER_COLLECTION_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesRepository @Inject constructor(
    private val firestoreDb: FirebaseFirestore,
    private val lendsumDatabase: LendsumDatabase,
    private val realTimeDb: DatabaseReference,
    private val dataSyncManager: DataSyncManager,
    private val firebaseStorageReference: StorageReference
){
    private val userList: MutableLiveData<List<User>> = MutableLiveData()
    private val firebaseStorageImageUri: MutableLiveData<Uri> = MutableLiveData()

    fun getCurrentCachedUser(userId: String): Flow<User?> {
        return lendsumDatabase.getUserDao().getUser(userId)
    }

    fun getCurrentMessages(chatRoomId: String): Flow<List<Message>> {
        return lendsumDatabase.getChatMessageDao().getChatRoomMessages(chatRoomId)
    }

    fun getCurrentChatRoom(chatRoomId: String): Flow<ChatRoom>{
        return lendsumDatabase.getChatRoomDao().getChatRoom(chatRoomId)
    }

    fun findUserInFirestore(username: String){
        firestoreDb.collection(FIREBASE_USER_COLLECTION_PATH).whereEqualTo(FIREBASE_IS_PROFILE_PUBLIC_KEY, true).orderBy(
            FIREBASE_USERNAME_KEY).startAt( "@$username").endAt("$username\uf8ff").limit(3).get()
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

    fun syncMessagesData(chatId: String, viewModelScope: CoroutineScope){
        dataSyncManager.syncMessagesData(chatId, viewModelScope)
    }

    fun uploadProfilePhotoToFirebaseStorage(chatId: String, fileName: String, uri: Uri) {

        val chatRoomImgRef = firebaseStorageReference.child("chat_room_images").child(chatId).child(fileName)

        val uploadTask = chatRoomImgRef.putFile(uri)
        uploadTask.addOnCompleteListener{ task->
            if(task.isSuccessful){
                Log.d(TAG, "Message pic uploaded to storage")
                getMsgImgLink(chatRoomImgRef)
            }else{
                Log.d(TAG, "Message pic failed to upload to storage ${task.exception}")
            }
        }
    }

    private fun getMsgImgLink(chatRoomImgRef: StorageReference) {
        chatRoomImgRef.downloadUrl.addOnSuccessListener {
            Log.d(TAG, "Link: $it")
        }.addOnFailureListener {
            Log.d(TAG, "Couldn't get msg img link: $it")
        }
    }

    companion object{
        private val TAG = MessagesRepository::class.simpleName
    }
}