package com.lendsumapp.lendsum.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey @ColumnInfo(name = "chatRoomId") var chatRoomId: String,
    @ColumnInfo(name = "participants") var participants : List<User>,
    @ColumnInfo(name = "listOfMessages") var listOfMessages : List<ChatMessage>,
    @ColumnInfo(name = "lastTimestamp") var lastTimestamp: Long
)