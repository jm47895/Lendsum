package com.lendsumapp.lendsum.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.material.snackbar.Snackbar
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.util.GlobalConstants.PASSWORD_PATTERN
import java.text.DateFormat
import java.time.ZonedDateTime
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class AndroidUtils{

    companion object {
        fun showSnackBar(activity: Activity, msg: String) {

            Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Dismiss") {
                }.setActionTextColor(ContextCompat.getColor(activity, R.color.colorSecondaryLight))
                .show()
        }

        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view: View? = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
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

        fun hideView(view: View) {
            view.visibility = View.INVISIBLE
        }

        fun showView(view: View) {
            view.visibility = View.VISIBLE
        }

        fun goneView(view: View) {
            view.visibility = View.GONE
        }

        fun getTimestampInstant(): Long{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ZonedDateTime.now().toInstant().toEpochMilli()
            } else {
                Date().time
            }
        }

        fun convertTimestampToFullDate(timeStamp: Long): String{
            return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(timeStamp)
        }

        fun convertTimestampToShortDate(timeStamp: Long): String{

            val formattedDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(timeStamp)

            return formattedDate.substring(0, formattedDate.indexOf(","))
        }

        fun editSharedPrefs(sharedPrefs: SharedPreferences, key: String, value: Any){
            when(value){
                is String -> sharedPrefs.edit{ putString(key, value) }
                is Boolean -> sharedPrefs.edit{ putBoolean(key, value) }
                is Int -> sharedPrefs.edit{ putInt(key, value) }
                is Float -> sharedPrefs.edit{ putFloat(key, value)}
                is Long -> sharedPrefs.edit{ putLong(key, value)}
            }
        }
    }
}