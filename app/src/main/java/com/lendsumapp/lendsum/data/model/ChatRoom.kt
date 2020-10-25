package com.lendsumapp.lendsum.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "chat_rooms")
data class ChatRoom(
    @PrimaryKey @ColumnInfo(name = "chatRoomId") var chatRoomId: String,
    @ColumnInfo(name = "participants") var participants : @RawValue List<User>?,
    @ColumnInfo(name = "listOfMessages") var listOfMessages : @RawValue MutableList<Message>?,
    @ColumnInfo(name = "lastMessage") var lastTimestamp: String?
) : Parcelable{
    constructor(): this("",null,null,null)
}