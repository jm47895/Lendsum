package com.lendsumapp.lendsum.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import java.io.File
import javax.inject.Inject


class DatabaseUtils{
    companion object {
        fun getFileExtension(context: Context, uri: Uri): String{
            val contentResolver = context.contentResolver
            val mime = MimeTypeMap.getSingleton()
            return mime.getExtensionFromMimeType(contentResolver?.getType(uri)).toString()
        }
    }
}