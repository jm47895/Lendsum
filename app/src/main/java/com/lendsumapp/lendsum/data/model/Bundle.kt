package com.lendsumapp.lendsum.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "bundles")
data class Bundle (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "bundleId")val bundleId: Long,
    @ColumnInfo(name = "lenderName") var lenderName: String,
    @ColumnInfo(name = "borrowerName") var borrowerName: String,
    @ColumnInfo(name = "bundleTitle") var bundleTitle: String,
    @ColumnInfo(name = "bundleDescription") var bundleDescription: String,
    @ColumnInfo(name = "bundleItemList") var bundleItemList: List<String>?,
    @ColumnInfo(name = "lendTimestamp") var lendDate: Long,
    @ColumnInfo(name = "returnTimestamp") var returnDate: Long?,
    @ColumnInfo(name = "maturityTimestamp") var maturityDate: Long?,
    @ColumnInfo(name = "bundleRate") var bundleRate: String?,
    @ColumnInfo(name = "bundlePeriod") var bundlePeriod: String?,
    @ColumnInfo(name = "lateFee") var lateFee: Int?,
    @ColumnInfo(name = "imagePaths") var imagePaths: List<String>?,
    @ColumnInfo(name = "isLendToOwn") var isLendToOwn: Boolean?,
    @ColumnInfo(name = "isIndefinite") var isIndefinite: Boolean,
    @ColumnInfo(name = "isLending") var isLending: Boolean,
    @ColumnInfo(name = "isPublic") var isPublic: Boolean

)