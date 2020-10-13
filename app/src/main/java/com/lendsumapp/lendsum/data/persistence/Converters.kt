package com.lendsumapp.lendsum.data.persistence

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lendsumapp.lendsum.data.model.ChatMessage
import com.lendsumapp.lendsum.data.model.User

class Converters {

    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        return stringListString.split(",").map { it }
    }

    @TypeConverter
    fun toString(stringList: List<String>?): String {
        return stringList?.joinToString(separator = ",").toString()
    }

    @TypeConverter
    fun fromUserList(value: List<User>): String {
        val gson = Gson()
        val type = object : TypeToken<List<User>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toUserList(value: String): List<User> {
        val gson = Gson()
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromChatMessageList(value: List<ChatMessage>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toChatMessageList(value: String): List<ChatMessage> {
        val gson = Gson()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(value, type)
    }

}