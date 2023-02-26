package com.lendsumapp.lendsum.data.persistence

import androidx.room.*
import com.lendsumapp.lendsum.data.model.Bundle
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User): Int

    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getUser(userId: String): Flow<User?>

    @Delete
    suspend fun deleteUser(user: User)
}