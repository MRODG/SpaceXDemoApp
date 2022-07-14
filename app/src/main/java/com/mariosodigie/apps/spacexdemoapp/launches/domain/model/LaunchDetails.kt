package com.mariosodigie.apps.spacexdemoapp.launches.domain.model

data class LaunchDetails(
    val rocketName: String,
    val date: String,
    val success: Boolean,
    val imageUrl: String
)
