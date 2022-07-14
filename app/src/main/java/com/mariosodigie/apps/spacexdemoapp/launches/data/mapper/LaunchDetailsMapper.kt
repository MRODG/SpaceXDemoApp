package com.mariosodigie.apps.spacexdemoapp.launches.data.mapper

import com.mariosodigie.apps.spacexdemoapp.launches.data.local.LaunchDetailsEntity
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.LaunchDetailsDto
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.RocketRefDto
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.LaunchDetails
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.RocketRef

fun RocketRefDto.toRocketRef(): RocketRef {
    return RocketRef(
        name = name!!,
        id = id!!,
    )
}

fun LaunchDetailsDto.toLaunchDetailsEntity(): LaunchDetailsEntity{
    return LaunchDetailsEntity(
        rocketName = rocketName!!,
        date = date!!,
        success = success!!,
        imageUrl = media!!.patch!!.small!!
    )
}

fun LaunchDetailsEntity.toLaunchDetails(): LaunchDetails {
    return LaunchDetails(
        rocketName = rocketName,
        date = formatDate(date),
        success = success,
        imageUrl = imageUrl
    )
}

/**
 * Extract the year, month and day parts from the date and reorder into a new string
 */
private fun formatDate(date: String): String = with(date){ substring(8,10) + substring(4,8) + substring(0,4) }

