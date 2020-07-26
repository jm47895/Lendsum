package com.lendsumapp.lendsum.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun itemListToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToItemList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}