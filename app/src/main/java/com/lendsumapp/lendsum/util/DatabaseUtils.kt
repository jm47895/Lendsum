package com.lendsumapp.lendsum.util

import android.content.Context
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import java.io.File
import javax.inject.Inject


class DatabaseUtils{
    companion object {
        fun doesCacheDatabaseExist(context: Context, dbName: String): Boolean {
            val dbFile: File = context.getDatabasePath(dbName)
            return dbFile.exists()
        }
    }
}