package com.lendsumapp.lendsum.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.DatabaseUtils
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_COLLECTION_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_CHAT_ROOM_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_MESSAGES_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_USER_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class DataSyncManager @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val firestoreDb: FirebaseFirestore,
    private val realTimeDb: DatabaseReference
){

    val realtimeChatIdList = MutableLiveData<MutableList<String>>()
    val chatIdsList = mutableListOf<String>()

    fun doesLendsumDbExist(context: Context, dbName: String): Boolean{
        return DatabaseUtils.doesCacheDatabaseExist(context, dbName)
    }

    fun registerRealtimeChatIdListener(uid: String){

        val chatIdRef = realTimeDb.child(REALTIME_DB_USER_PATH).child(uid)

        chatIdRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    chatIdsList.add(data.value.toString())
                    realtimeChatIdList.postValue(chatIdsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error retrieving chatIds ${error.message}")
            }
        })
    }

    fun getRealtimeChatIds(): MutableLiveData<MutableList<String>> {
        return realtimeChatIdList
    }

    //Sync user data
    fun syncUserData(uid: String, scope: CoroutineScope){
        syncAllUserDataFromFirestore(uid, scope)
    }

    private fun syncAllUserDataFromFirestore(uid: String, viewModelScope: CoroutineScope){

        firestoreDb.collection(FIRESTORE_USER_COLLECTION_PATH)
            .document(uid)
            .get().addOnSuccessListener { document ->
                if(document != null){
                    Log.d(TAG, "Retrieved Existing User Firestore Document")
                    viewModelScope.launch(Dispatchers.IO) {
                        syncUserDataInLocalCache(document.toObject<User>()!!)
                    }
                }else{
                    /*This will only be hit if we lose all of our remote user data, which in this case
                    * we should have back up data to look at*/
                    Log.d(TAG, "Existing User Document Does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, exception.toString())
            }
    }

    private suspend fun syncUserDataInLocalCache(user: User){
        lendsumDatabase.getUserDao().insertUser(user)
    }
    //End sync user data

    //Sync chat data
    fun syncChatRoomList(listOfChatIds: List<String>, viewModelScope: CoroutineScope){
        for (chatId in listOfChatIds){
            val chatRoomRef = realTimeDb.child(REALTIME_DB_CHAT_ROOM_PATH).child(chatId).ref

            chatRoomRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatRoom = snapshot.getValue(ChatRoom::class.java)
                    viewModelScope.launch {
                        chatRoom?.let { syncChatRoomDataInLocalCache(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "There was a databaseError: $error")
                }


            })
        }
    }

    suspend fun syncChatRoomDataInLocalCache(chatRoom: ChatRoom){
        lendsumDatabase.getChatRoomDao().insertChatRoom(chatRoom)
    }
    //End sync chat data

    //Sync message data
    fun syncMessagesData(chatId: String, viewModelScope: CoroutineScope){

        val messagesRef = realTimeDb.child(REALTIME_DB_MESSAGES_PATH).child(chatId).ref

        messagesRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                Log.d(TAG, "Message added: " + snapshot.value.toString())

                val message = snapshot.getValue(Message::class.java)
                viewModelScope.launch(Dispatchers.IO){
                    message?.let { syncMessagesDataInLocalCache(it) }
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    suspend fun syncMessagesDataInLocalCache(message: Message){
        lendsumDatabase.getChatMessageDao().insertChatMessage(message)
    }
    //End sync message data

    companion object{
        private val TAG = DataSyncManager::class.java.simpleName
    }
}
