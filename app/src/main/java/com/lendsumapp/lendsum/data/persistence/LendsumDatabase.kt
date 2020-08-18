package com.lendsumapp.lendsum.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lendsumapp.lendsum.data.model.Bundle
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.BundleDao
import com.lendsumapp.lendsum.data.persistence.Converters

@Database(entities = arrayOf(Bundle::class, User::class), version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class LendsumDatabase : RoomDatabase(){
    abstract fun getBundleDao(): BundleDao
    abstract fun getUserDao(): UserDao
}