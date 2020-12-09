package com.lendsumapp.lendsum.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

import java.util.*

@Entity(tableName = "chat_messages")
data class  Message(
    @PrimaryKey @ColumnInfo(name = "timestamp") var messageTimestamp: Long,
    @ColumnInfo(name = "chatRoomId") var chatRoomId: String,
    @ColumnInfo(name = "sender") var messageSender: String,
    @ColumnInfo(name = "message") var message: String,
    @ColumnInfo(name = "listOfImageUris") var listOfImageUris: List<String>?,
    @get:Exclude @ColumnInfo(name = "sentToRemoteDb") var sentToRemoteDb: Boolean = false
){
    constructor(): this(0, "", "",  "", null)
}