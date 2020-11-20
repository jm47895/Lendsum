package com.lendsumapp.lendsum.data.persistence

import androidx.room.*
import com.lendsumapp.lendsum.data.model.ChatRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoom: ChatRoom): Long

    @Update
    suspend fun updateChatRoom(chatRoom: ChatRoom): Int

    @Delete
    suspend fun deleteChatRoom(chatRoom: ChatRoom)

    @Query("SELECT * FROM chat_rooms ORDER BY lastTimestamp DESC")
    fun getAllChatRooms(): Flow<List<ChatRoom>>
}