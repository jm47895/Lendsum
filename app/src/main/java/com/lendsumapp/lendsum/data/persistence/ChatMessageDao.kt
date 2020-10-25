package com.lendsumapp.lendsum.data.persistence

import androidx.room.*
import com.lendsumapp.lendsum.data.model.Message

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insertChatMessage(message: Message): Long

    @Update
    suspend fun updateChatMessage(message: Message): Int

    @Delete
    suspend fun deleteChatMessage(message: Message)

    @Query("SELECT * FROM chat_messages WHERE chatRoom = :chatRoomId")
    suspend fun getChatRoomMessages(chatRoomId: String): List<Message>
}