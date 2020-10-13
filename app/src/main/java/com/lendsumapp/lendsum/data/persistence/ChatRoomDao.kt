package com.lendsumapp.lendsum.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.lendsumapp.lendsum.data.model.ChatRoom

@Dao
interface ChatRoomDao {

    @Insert
    suspend fun insertChatRoom(chatRoom: ChatRoom): Long

    @Update
    suspend fun updateChatRoom(chatRoom: ChatRoom): Int

    @Delete
    suspend fun deleteChatRoom(chatRoom: ChatRoom)

}