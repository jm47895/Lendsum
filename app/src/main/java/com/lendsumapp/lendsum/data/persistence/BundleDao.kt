package com.lendsumapp.lendsum.data.persistence

import androidx.room.*
import com.lendsumapp.lendsum.data.model.Bundle

@Dao
interface BundleDao {

    @Insert
    suspend fun insertBundle(bundle: Bundle): Long

    @Update
    suspend fun updateBundle(bundle: Bundle): Int

    @Delete
    suspend fun deleteBundle(vararg bundle: Bundle)

    @Query("SELECT * FROM bundles WHERE bundleId = :bundleId")
    suspend fun getBundleById(bundleId : Long) : Bundle

    @Query("SELECT * FROM bundles")
    suspend fun getAllBundles(): List<Bundle>

    @Query("SELECT * FROM bundles WHERE isLending = 1")
    suspend fun getLendBundles() : List<Bundle>

    @Query("SELECT * FROM bundles WHERE isLending = 0")
    suspend fun getBorrowBundles(): List<Bundle>

    @Query("SELECT * FROM bundles WHERE lenderName = :lenderName")
    suspend fun findLender(lenderName: String) : List<Bundle>

    @Query("DELETE FROM bundles")
    suspend fun deleteAllBundles()
    
}