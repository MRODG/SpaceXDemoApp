package com.mariosodigie.apps.spacexdemoapp.launches.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LaunchDetailsEntity(
    val rocketName: String,
    val date: String,
    val success: Boolean,
    val imageUrl: String,
    @PrimaryKey val id: Int? = null
)
