package com.lendsumapp.lendsum.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "userId")
    val userId: String = "",
    @ColumnInfo(name = "name")
    val name: String = "",
    @get: Exclude
    @ColumnInfo(name = "username")
    val username: String = "",
    @get: Exclude
    @ColumnInfo(name = "email")
    val email: String = "",
    @get: Exclude
    @ColumnInfo(name = "phoneNumber")
    val phoneNumber: String = "",
    @ColumnInfo(name = "profilePicUri")
    val profilePicUri: String? = "",
    @get: Exclude
    @ColumnInfo(name = "karmaScore")
    val karmaScore: Int = 0,
    //TODO Change to type Friend object when feature is implemented
    @get: Exclude
    @ColumnInfo(name = "friendsList")
    val friendList: List<String>? = emptyList(),
    @get: Exclude
    @ColumnInfo(name = "isProfilePublic")
    val isProfilePublic: Boolean = true
)