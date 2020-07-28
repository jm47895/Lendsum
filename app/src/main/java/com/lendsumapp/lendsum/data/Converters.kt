package com.lendsumapp.lendsum.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun toString(stringList: List<String>?): String {
        return stringList?.joinToString(separator = ",").toString()
    }

    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        return stringListString.split(",").map { it }
    }

}