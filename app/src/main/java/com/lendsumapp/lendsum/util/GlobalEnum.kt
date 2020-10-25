package com.lendsumapp.lendsum.util

import androidx.annotation.Keep

@Keep enum class NavSignUpType {
    EMAIL_LOGIN,
    GOOGLE_LOGIN,
    FACEBOOK_LOGIN
}

@Keep enum class EditProfileInfoType{
    PROFILE_NAME,
    PROFILE_USERNAME,
    PROFILE_EMAIL,
    PROFILE_VISIBILITY
}
