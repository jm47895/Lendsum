package com.lendsumapp.lendsum.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "bundles")
data class Bundle (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "bundleId")val bundleId: Long,
    @ColumnInfo(name = "lenderName") val lenderName: String,
    @ColumnInfo(name = "borrowerName") val borrowerName: String,
    @ColumnInfo(name = "bundleTitle") val bundleTitle: String,
    @ColumnInfo(name = "bundleDescription") val bundleDescription: String,
    @ColumnInfo(name = "bundleItemList") val bundleItemList: List<String>?,
    @ColumnInfo(name = "lendTimestamp") val lendDate: Long,
    @ColumnInfo(name = "returnTimestamp") var returnDate: Long?,
    @ColumnInfo(name = "maturityTimestamp") val maturityDate: Long?,
    @ColumnInfo(name = "bundleRate") val bundleRate: String?,
    @ColumnInfo(name = "bundlePeriod") val bundlePeriod: String?,
    @ColumnInfo(name = "lateFee") val lateFee: Int?,
    @ColumnInfo(name = "imagePaths") val imagePaths: List<String>?,
    @ColumnInfo(name = "isLendToOwn") val isLendToOwn: Boolean?,
    @ColumnInfo(name = "isIndefinite") val isIndefinite: Boolean,
    @ColumnInfo(name = "isLending") val isLending: Boolean

)