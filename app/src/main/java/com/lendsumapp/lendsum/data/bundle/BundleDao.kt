package com.lendsumapp.lendsum.data.bundle

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BundleDao {

    @Insert
    suspend fun insertBundle(vararg bundles: Bundle)

    @Query("SELECT * FROM bundles WHERE isLending = 1")
    suspend fun getAllLendBundles() : List<Bundle>
}