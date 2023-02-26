package com.lendsumapp.lendsum.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "bundles")
data class Bundle (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "bundleId")var bundleId: Long = 123456,
    @ColumnInfo(name = "lenderName") var lenderName: String = "",
    @ColumnInfo(name = "borrowerName") var borrowerName: String = "",
    @ColumnInfo(name = "bundleTitle") var bundleTitle: String = "",
    @ColumnInfo(name = "bundleDescription") var bundleDescription: String = "",
    @ColumnInfo(name = "bundleItemList") var bundleItemList: List<String>? = null,
    @ColumnInfo(name = "lendTimestamp") var lendDate: Long = 12345,
    @ColumnInfo(name = "returnTimestamp") var returnDate: Long?= null,
    @ColumnInfo(name = "maturityTimestamp") var maturityDate: Long? = null,
    @ColumnInfo(name = "bundleRate") var bundleRate: String? = null,
    @ColumnInfo(name = "bundlePeriod") var bundlePeriod: String? = null,
    @ColumnInfo(name = "lateFee") var lateFee: Int = 0,
    @ColumnInfo(name = "imagePaths") var imagePaths: List<String>? = null,
    @ColumnInfo(name = "isLendToOwn") var isLendToOwn: Boolean = false,
    @ColumnInfo(name = "isIndefinite") var isIndefinite: Boolean = false,
    @ColumnInfo(name = "isLending") var isLending: Boolean = false,
    @ColumnInfo(name = "isPublic") var isPublic: Boolean = true

)