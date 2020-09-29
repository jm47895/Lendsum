package com.lendsumapp.lendsum.data.persistence

import androidx.room.*
import com.lendsumapp.lendsum.data.model.Bundle
import com.lendsumapp.lendsum.data.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User): Int

    @Query("SELECT * FROM user WHERE userId = :userId")
    suspend fun getUser(userId: String): User

    @Delete
    suspend fun deleteUser(user: User)
}