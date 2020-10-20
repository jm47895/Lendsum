package com.lendsumapp.lendsum.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey @ColumnInfo(name = "userId") var userId: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "phoneNumber") var phoneNumber: String,
    @ColumnInfo(name = "profilePicUri") var profilePicUri: String?,
    @ColumnInfo(name = "karmaScore") var karmaScore: Int,
    //TODO Change to type Friend object when feature is implemented
    @ColumnInfo(name = "friendsList") var friendList: List<String>?,
    @ColumnInfo(name = "isProfilePublic") var isProfilePublic: Boolean
){
    constructor() : this("", "", "","","","",0, emptyList(), true)
}