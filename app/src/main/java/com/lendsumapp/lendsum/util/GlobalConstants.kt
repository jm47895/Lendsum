package com.lendsumapp.lendsum.util

object GlobalConstants {

    /*Shared Pref keys*/
    const val NAV_SIGN_UP_TYPE = "navSignUpType"
    const val RETURNING_USER = "returningUser"
    const val NUMBER_VERIFIED = "numberVerified"

    /*Firestore Constants*/
    const val FIRESTORE_USER_COLLECTION_PATH = "users"
    const val FIRESTORE_EMAIL_KEY = "email"
    const val FIRESTORE_USERNAME_KEY = "username"
    const val FIRESTORE_PROFILE_NAME_KEY = "name"
    const val FIRESTORE_PROFILE_PIC_URI_KEY = "profilePicUri"
    /*LEAVE THIS CONSTANT ALONE. For some reason, Firestore is renaming this key when
    * an object is sent to the db to the key below*/
    const val FIRESTORE_IS_PROFILE_PUBLIC_KEY = "profilePublic"

    /*Android specific constants*/
    //Pattern is for One upper case, one lower case, one symbol, and between 6 and 20 characters
    const val PASSWORD_PATTERN = "^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@%+/\'!#$^?:,(){}~_.]).{6,20})$"
    //Fragment data passing keys
    const val CHAT_ROOM_REQUEST_KEY = "chatRoomRequestKey"
    const val CHAT_ROOM_BUNDLE_KEY = "chatRoomBundleKey"

    /*Firebase real-time database constants*/
    const val REALTIME_DB_USER_PATH = "users"
    const val REALTIME_DB_MESSAGES_PATH = "messages"
    const val REALTIME_DB_CHAT_ROOM_PATH = "chat_rooms"
}