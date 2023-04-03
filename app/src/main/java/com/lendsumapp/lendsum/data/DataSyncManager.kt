package com.lendsumapp.lendsum.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.lendsumapp.lendsum.data.model.*
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USER_COLLECTION_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_CHAT_ROOM_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_MESSAGES_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_USER_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class DataSyncManager @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val firestoreDb: FirebaseFirestore,
    private val realTimeDb: DatabaseReference
){

    val realtimeChatIdList = MutableLiveData<MutableList<String>>()
    val chatIdsList = mutableListOf<String>()

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
                Log.e(TAG, "Error retrieving chatIds ${error.message}")
            }
        })
    }

    fun getRealtimeChatIds(): MutableLiveData<MutableList<String>> {
        return realtimeChatIdList
    }

    //Sync user data
    suspend fun syncAllUserDataFromFirestore(uid: String): Response<User> {

        return suspendCoroutine{ continuation ->

            firestoreDb.collection(FIREBASE_USER_COLLECTION_PATH)
                .document(uid)
                .get().addOnSuccessListener { document ->
                    if(document != null){
                        Log.i(TAG, "Retrieved Existing User Firestore Document ${document.toObject(User::class.java)}")
                        continuation.resume(Response(status = Status.SUCCESS, data = document.toObject(User::class.java)))
                    }else{
                        /*This will only be hit if we lose all of our remote user data, which in this case
                        * we should have back up data to look at*/
                        Log.e(TAG, "Existing User Document Does not exist")
                        continuation.resume(Response(status = Status.ERROR))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Response(status = Status.ERROR))
                    Log.e(TAG, exception.toString())
                }
        }
    }

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
                    Log.e(TAG, "There was a databaseError: $error")
                }


            })
        }
    }

    suspend fun syncChatRoomDataInLocalCache(chatRoom: ChatRoom){
        lendsumDatabase.getChatRoomDao().insertChatRoom(chatRoom)
    }


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

    companion object{
        private val TAG = DataSyncManager::class.java.simpleName
    }
}
