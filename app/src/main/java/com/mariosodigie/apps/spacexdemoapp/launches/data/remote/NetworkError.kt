package com.mariosodigie.apps.spacexdemoapp.launches.data.remote

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mariosodigie.apps.spacexdemoapp.R

enum class NetworkError(@StringRes val title: Int, @StringRes val message: Int, @DrawableRes val icon: Int? = null) {

    Generic(R.string.error_title_generic, R.string.error_message_generic),
    PhoneOffline(R.string.phone_error_title, R.string.phone_error_message),
    RocketRefNotFound(R.string.rocket_reference_error_title, R.string.rocker_reference_error_message),
    LaunchListFailure(R.string.launch_list_error_title, R.string.launch_list_error_message)
}