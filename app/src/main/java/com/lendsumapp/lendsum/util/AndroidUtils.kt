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
import com.lendsumapp.lendsum.ui.CreateAccountFragment
import com.lendsumapp.lendsum.util.GlobalConstants.PASSWORD_PATTERN
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@ActivityScoped
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

    fun isValidPassword(password: String): Boolean {
        val matchCase: Matcher
        val isValid: Boolean
        val pattern: Pattern = Pattern.compile(PASSWORD_PATTERN)
        matchCase = pattern.matcher(password)
        isValid = matchCase.matches()
        return isValid
    }

    fun hideView(view: View){
        view.visibility = View.INVISIBLE
    }

    fun showView(view: View){
        view.visibility = View.VISIBLE
    }
}