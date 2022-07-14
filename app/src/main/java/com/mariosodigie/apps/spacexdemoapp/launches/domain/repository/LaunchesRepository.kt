package com.mariosodigie.apps.spacexdemoapp.launches.domain.repository

import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.LaunchDetails
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.Result
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.RocketRef
import kotlinx.coroutines.flow.Flow

interface LaunchesRepository {

    suspend fun getRocketRef(rocketSearch: String): Result<RocketRef>

    suspend fun getLaunches(refresh: Boolean = false, id: String): Flow<Result<List<LaunchDetails>>>

}