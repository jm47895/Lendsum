package com.lendsumapp.lendsum.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.lendsumapp.lendsum.data.model.ChatMessage

@Dao
interface ChatMessageDao {

    @Insert
    suspend fun insertChatMessage(chatMessage: ChatMessage): Long

    @Update
    suspend fun updateChatMessage(chatMessage: ChatMessage): Int

    @Delete
    suspend fun deleteChatMessage(chatMessage: ChatMessage)
}