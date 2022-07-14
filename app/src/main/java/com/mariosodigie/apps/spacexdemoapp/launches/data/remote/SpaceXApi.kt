package com.mariosodigie.apps.spacexdemoapp.launches.data.remote

import com.mariosodigie.apps.spacexdemoapp.BuildConfig
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.LaunchDetailsDto
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.RocketRefDto
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface SpaceXApi {

    @GET("v4/rockets")
    suspend fun requestRocketsRef(): List<RocketRefDto>

    @GET("/v5/launches")
    suspend fun requestLaunches(): List<LaunchDetailsDto>

    companion object {

        fun get(baseUrl: String = BuildConfig.DEV_BASE_URL): SpaceXApi {

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build().create(SpaceXApi::class.java)

        }
    }

}