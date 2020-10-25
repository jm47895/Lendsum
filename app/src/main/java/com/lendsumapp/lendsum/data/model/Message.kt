package com.lendsumapp.lendsum.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "chat_messages")
data class Message(
    @PrimaryKey @ColumnInfo(name = "timestamp") var messageTimestamp: String,
    @ColumnInfo(name = "chatRoom") var chatRoom: String,
    @ColumnInfo(name = "sender") var messageSender: String,
    @ColumnInfo(name = "guestPic") var guestPic: String?,
    @ColumnInfo(name = "message") var message: String,
    @ColumnInfo(name = "listOfImageUris") var listOfImageUris: List<String>?
)