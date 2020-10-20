package com.lendsumapp.lendsum.util

object GlobalConstants {

    /*Shared Pref keys*/
    const val NAV_SIGN_UP_TYPE = "navSignUpType"
    const val RETURNING_USER = "returningUser"
    const val NUMBER_VERIFIED = "numberVerified"

    /*Firestore Constants*/
    const val USER_COLLECTION_PATH = "users"
    const val EMAIL_KEY = "email"
    const val USERNAME_KEY = "username"
    const val PROFILE_NAME_KEY = "name"
    const val PROFILE_PIC_URI_KEY = "profilePicUrl"
    const val IS_PROFILE_PUBLIC_KEY = "isPublic"
    /*Android specific constants*/
    //Pattern is for One upper case, one lower case, one symbol, and between 6 and 20 characters
    const val PASSWORD_PATTERN = "^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@%+/\'!#$^?:,(){}~_.]).{6,20})$"
}