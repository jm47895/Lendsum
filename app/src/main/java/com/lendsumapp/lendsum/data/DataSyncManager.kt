package com.lendsumapp.lendsum.data

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.lendsumapp.lendsum.data.model.ChatRoom
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.DatabaseUtils
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_COLLECTION_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_CHAT_ROOM_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_MESSAGES_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.REALTIME_DB_USER_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class DataSyncManager @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val firestoreDb: FirebaseFirestore,
    private val realTimeDb: DatabaseReference
){

    //Full data sync on reinstall for existing user methods
    fun doesLendsumDbExist(context: Context, dbName: String): Boolean{
        return DatabaseUtils.doesCacheDatabaseExist(context, dbName)
    }

    fun syncAllDataFromDatabases(uid: String){
        syncAllUserDataFromFirestore(uid)
        getListOfChatIdsFromRealtimeDb(uid)
    }

    private fun getListOfChatIdsFromRealtimeDb(uid: String){
        val chatIdRef = realTimeDb.child(REALTIME_DB_USER_PATH).child(uid).ref
        val listOfChatIds = mutableListOf<String>()

        chatIdRef.addListenerForSingleValueEvent(object: ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    listOfChatIds.add(data.value.toString())
                }
                Log.d(TAG, "chat id on changed hit")
                syncAllMessagesFromRealtimeDb(listOfChatIds)
                syncAllChatRoomDataFromRealtimeDb(listOfChatIds)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failure to retrieve chatIds from Realtime Database: $error")
            }

        })
    }


    private fun syncAllUserDataFromFirestore(uid: String){

        firestoreDb.collection(FIRESTORE_USER_COLLECTION_PATH)
            .document(uid)
            .get().addOnSuccessListener { document ->
                if(document != null){
                    Log.d(TAG, "Retrieved Existing User Firestore Document")
                    GlobalScope.launch(Dispatchers.IO) {
                        insertAllExistingUserDataIntoLocalCache(document.toObject<User>()!!)
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

    private suspend fun insertAllExistingUserDataIntoLocalCache(user: User){
        lendsumDatabase.getUserDao().insertUser(user)
    }

    private fun syncAllMessagesFromRealtimeDb(listOfChatIds: MutableList<String>) {

        for (chatIds in listOfChatIds){
            val messagesRef = realTimeDb.child(REALTIME_DB_MESSAGES_PATH).child(chatIds).ref
            messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children){
                        val msg: Message = data.getValue(Message::class.java)!!
                        GlobalScope.launch(Dispatchers.IO) {
                            insertAllExistingUserMessagesIntoLocaleCache(msg)
                        }
                    }
                    Log.d(TAG, "Message on data change hit")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, error.message)
                }
            })
        }
    }

    private suspend fun insertAllExistingUserMessagesIntoLocaleCache(message: Message){
        lendsumDatabase.getChatMessageDao().insertChatMessage(message)

    }

    private fun syncAllChatRoomDataFromRealtimeDb(listOfChatIds: MutableList<String>){
        for (chatIds in listOfChatIds){

            val chatRoomRef = realTimeDb.child(REALTIME_DB_CHAT_ROOM_PATH).child(chatIds).ref
            chatRoomRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatRoom = snapshot.getValue(ChatRoom::class.java)!!
                    GlobalScope.launch(Dispatchers.IO) {
                        insertAllExistingChatRoomsIntoLocaleCache(chatRoom)
                    }
                    Log.d(TAG, "Chat room on Data changed hit")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, error.message)
                }
            })
        }
    }

    private suspend fun insertAllExistingChatRoomsIntoLocaleCache(chatRoom: ChatRoom){
        lendsumDatabase.getChatRoomDao().insertChatRoom(chatRoom)
    }
    //End of full data sync on reinstall for existing user methods

    companion object{
        private val TAG = DataSyncManager::class.java.simpleName
    }
}
