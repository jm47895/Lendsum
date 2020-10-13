package com.lendsumapp.lendsum.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey @ColumnInfo(name = "chatMessageId") var chatMessageId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "chatRoom") var chatRoom: String,
    @ColumnInfo(name = "sender") var chatMessageSender: String,
    @ColumnInfo(name = "senderPic") var senderPic: String?,
    @ColumnInfo(name = "messageTimestamp") var messageTimestamp: Long,
    @ColumnInfo(name = "message") var message: String,
    @ColumnInfo(name = "listOfImageUris") var listOfImageUris: List<String>?
)