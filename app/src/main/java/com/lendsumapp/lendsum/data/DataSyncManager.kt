package com.lendsumapp.lendsum.data

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.lendsumapp.lendsum.data.model.Message
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.DatabaseUtils
import com.lendsumapp.lendsum.util.GlobalConstants.USER_COLLECTION_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class DataSyncManager @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val firestoreDb: FirebaseFirestore,
    private val realTimeDb: DatabaseReference
){
    private val listOfChatIds = mutableListOf<String>()

    //Full data sync on reinstall for existing user methods
    fun doesLendsumDbExist(context: Context, dbName: String): Boolean{
        return DatabaseUtils.doesCacheDatabaseExist(context, dbName)
    }

    fun syncAllDataFromDatabases(uid: String){
        syncAllUserDataFromFirestore(uid)
        syncAllMessagesDataFromRealtimeDb(uid)
    }

    private fun syncAllMessagesDataFromRealtimeDb(uid: String){
        val chatIdRef = realTimeDb.child("usr").child(uid).ref
        chatIdRef.addListenerForSingleValueEvent(chatIdsListener)
    }

    private val chatIdsListener =  object: ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            for (data in snapshot.children) {
                listOfChatIds.add(data.value.toString())
            }
            getMessagesFromRealtimeDb(listOfChatIds)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "Failure to retrieve messages from Realtime Database: $error")
        }

    }

    private fun getMessagesFromRealtimeDb(listOfChatIds: MutableList<String>) {
        for (chatIds in listOfChatIds){
            val messagesRef = realTimeDb.child("msg").child(chatIds).ref
            messagesRef.addListenerForSingleValueEvent(messageListener)
        }
    }

    private val messageListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            for (data in snapshot.children){
                val msg: Message = data.getValue(Message::class.java)!!
                GlobalScope.launch(Dispatchers.IO) {
                    insertAllExistingUserMessagesIntoLocaleCache(msg)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, error.message)
        }
    }

    private fun syncAllUserDataFromFirestore(uid: String){

        firestoreDb.collection(USER_COLLECTION_PATH)
            .document(uid)
            .get().addOnSuccessListener { document ->
                if(document != null){
                    Log.d(TAG, "Retrieved Existing User Firestore Document")
                    GlobalScope.launch(Dispatchers.IO) {
                        insertAllExistingUserIntoLocalCache(document.toObject<User>()!!)
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

    private suspend fun insertAllExistingUserIntoLocalCache(user: User){
        lendsumDatabase.getUserDao().insertUser(user)
        Log.d(TAG, "Existing user synced into local cache")
    }

    private suspend fun insertAllExistingUserMessagesIntoLocaleCache(message: Message){
        lendsumDatabase.getChatMessageDao().insertChatMessage(message)

        Log.d(TAG, "Existing message synced into local cache")
    }
    //End of full data sync on reinstall for existing user methods

    companion object{
        private val TAG = DataSyncManager::class.java.simpleName
    }
}
