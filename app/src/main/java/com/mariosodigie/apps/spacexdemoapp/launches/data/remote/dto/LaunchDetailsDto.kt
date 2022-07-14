package com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto

import com.squareup.moshi.Json

data class LaunchDetailsDto(
    @field:Json(name = "rocket") val rocketId: String?,
    @field:Json(name = "name") val rocketName: String?,
    @field:Json(name = "date_utc") val date: String?,
    @field:Json(name = "success") val success: Boolean?,
    @field:Json(name = "links") val media: Links?,
) {
    data class Links(
        @field:Json(name = "patch") val patch: Patch?,
    ){
        data class Patch(
            @field:Json(name = "small") val small: String?,
            @field:Json(name = "large") val large: String?,
        )
    }
}