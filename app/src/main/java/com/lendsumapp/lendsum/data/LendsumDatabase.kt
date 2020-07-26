package com.lendsumapp.lendsum.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lendsumapp.lendsum.data.bundle.Bundle
import com.lendsumapp.lendsum.data.bundle.BundleDao

@Database(entities = arrayOf(Bundle::class), version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class LendsumDatabase : RoomDatabase(){
    abstract fun bundleDao(): BundleDao
}