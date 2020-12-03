package com.lendsumapp.lendsum.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

@Entity(tableName = "user")
data class User(
    @PrimaryKey @ColumnInfo(name = "userId") var userId: String,
    @ColumnInfo(name = "name") var name: String,
    @get: Exclude @ColumnInfo(name = "username") var username: String,
    @get: Exclude @ColumnInfo(name = "email") var email: String,
    @get: Exclude @ColumnInfo(name = "phoneNumber") var phoneNumber: String,
    @ColumnInfo(name = "profilePicUri") var profilePicUri: String?,
    @get: Exclude @ColumnInfo(name = "karmaScore") var karmaScore: Int,
    //TODO Change to type Friend object when feature is implemented
    @get: Exclude @ColumnInfo(name = "friendsList") var friendList: List<String>?,
    @get: Exclude @ColumnInfo(name = "isProfilePublic") var isProfilePublic: Boolean
){
    constructor() : this("", "", "","","","",0, emptyList(), true)
}