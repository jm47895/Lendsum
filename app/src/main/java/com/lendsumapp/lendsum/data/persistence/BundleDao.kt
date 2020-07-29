package com.lendsumapp.lendsum.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lendsumapp.lendsum.data.model.Bundle

@Dao
interface BundleDao {

    @Insert
    suspend fun insertBundle(vararg bundle: Bundle)

    @Delete
    suspend fun deleteBundle(vararg bundle: Bundle)

    @Query("SELECT * FROM bundles WHERE isLending = 1")
    suspend fun getAllLendBundles() : List<Bundle>

    @Query("SELECT * FROM bundles WHERE lenderName = :lenderName")
    suspend fun findLender(lenderName: String) : List<Bundle>

    @Query("DELETE FROM bundles")
    suspend fun deleteAllBundles()
    
}