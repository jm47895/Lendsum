package com.lendsumapp.lendsum.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey @ColumnInfo(name = "chatRoomId") var chatRoomId: String,
    @ColumnInfo(name = "participants") var participants : @RawValue List<User>,
    @ColumnInfo(name = "lastMessage") var lastMessage: String,
    @ColumnInfo(name = "lastTimestamp") var lastTimestamp: Long
) : Parcelable{
    constructor(): this("", emptyList<User>(),"", 0)
}