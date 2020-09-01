package com.lendsumapp.lendsum.util

import android.app.Activity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.lendsumapp.lendsum.R
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class AndroidUtils @Inject constructor(){

    fun showSnackBar(activity: Activity, msg: String) {

        Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {
        }.setActionTextColor(ContextCompat.getColor(activity, R.color.colorSecondaryLight))
            .show()
    }
}