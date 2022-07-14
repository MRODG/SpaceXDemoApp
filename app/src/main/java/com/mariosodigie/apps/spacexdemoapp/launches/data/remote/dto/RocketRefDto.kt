package com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto

import com.squareup.moshi.Json

data class RocketRefDto(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "id") val id: String?
)
