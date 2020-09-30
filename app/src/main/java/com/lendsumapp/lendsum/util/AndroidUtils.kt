package com.lendsumapp.lendsum.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.lendsumapp.lendsum.R
import dagger.hilt.android.scopes.FragmentScoped
import java.io.File
import javax.inject.Inject

@FragmentScoped
class AndroidUtils @Inject constructor(){

    fun showSnackBar(activity: Activity, msg: String) {

        Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {
        }.setActionTextColor(ContextCompat.getColor(activity, R.color.colorSecondaryLight))
            .show()
    }

    fun hideKeyboard(context: Context, view: View){
        val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun doesDatabaseExist(context: Context, dbName: String): Boolean{
        val dbFile: File = context.getDatabasePath(dbName)
        return dbFile.exists()
    }
}