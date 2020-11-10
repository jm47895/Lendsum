package com.lendsumapp.lendsum.data.persistence

import androidx.room.*
import com.lendsumapp.lendsum.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insertChatMessage(message: Message): Long

    @Update
    suspend fun updateChatMessage(message: Message): Int

    @Delete
    suspend fun deleteChatMessage(message: Message)

    @Query("SELECT * FROM chat_messages WHERE chatRoomId = :chatRoomId ORDER BY timestamp ASC")
    fun getChatRoomMessages(chatRoomId: String): Flow<List<Message>>
}