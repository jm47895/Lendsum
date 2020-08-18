package com.lendsumapp.lendsum.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.lendsumapp.lendsum.data.model.Bundle
import com.lendsumapp.lendsum.data.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User): Int

    @Delete
    suspend fun deleteUser(user: User)
}