package com.lendsumapp.lendsum.util

object GlobalConstants {

    /*Shared Pref keys*/
    const val NAV_SIGN_UP_TYPE = "navSignUpType"
    const val RETURNING_USER = "returningUser"
    const val NUMBER_VERIFIED = "numberVerified"

    /*Firestore Constants*/
    const val FIREBASE_USER_COLLECTION_PATH = "users"
    const val FIREBASE_EMAIL_KEY = "email"
    const val FIREBASE_PASS_KEY = "password"
    const val FIREBASE_USERNAME_KEY = "username"
    const val FIREBASE_PROFILE_NAME_KEY = "name"
    const val FIREBASE_PROFILE_PIC_URI_KEY = "profilePicUri"
    const val FIREBASE_IS_PROFILE_PUBLIC_KEY = "profilePublic"

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

    /*Firebase storage constants*/
    const val FIREBASE_STORAGE_PROFILE_PIC_PATH = "profile_pics"

    /*Work manager constants*/
    const val UPLOAD_PROF_PIC_NAME_KEY = "profile_pic_file_name"
    const val UPLOAD_PROF_PIC_URI_KEY = "profile_pic_uri"
    const val UPLOAD_PROFILE_PIC_WORKER = "upload_profile_image_worker"
    const val UPDATE_USER_PROF_WORKER = "update_user_profile_worker"
    const val UPDATE_USER_CACHE_WORKER_KEY = "update_user_cache_worker"
    const val FIRESTORE_USER_WORKER_MAP_KEY = "firestore_user_key"
    const val FIRESTORE_USER_WORKER_MAP_VALUE = "firestore_user_value"
    const val FIREBASE_AUTH_UPDATE_MAP_KEY = "firebase_auth_key"
    const val FIREBASE_AUTH_UPDATE_MAP_VALUE = "firebase_auth_value"

    /*Lendsum files constants*/
    const val LENDSUM_PROFILE_PIC_DIR_PATH = "lendsum_images/profile_images"
}