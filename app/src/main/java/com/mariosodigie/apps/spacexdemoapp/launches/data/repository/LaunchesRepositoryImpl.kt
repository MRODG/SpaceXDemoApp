package com.mariosodigie.apps.spacexdemoapp.launches.data.repository

import com.mariosodigie.apps.spacexdemoapp.launches.data.local.LaunchesDatabase
import com.mariosodigie.apps.spacexdemoapp.launches.data.mapper.toLaunchDetails
import com.mariosodigie.apps.spacexdemoapp.launches.data.mapper.toLaunchDetailsEntity
import com.mariosodigie.apps.spacexdemoapp.launches.data.mapper.toRocketRef
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.SpaceXApi
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.LaunchDetailsDto
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.RocketRefDto
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.LaunchDetails
import com.mariosodigie.apps.spacexdemoapp.launches.domain.repository.LaunchesRepository
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.NetworkError
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.Result
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.RocketRef
import com.mariosodigie.apps.spacexdemoapp.launches.utis.ConnectivityCheck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LaunchesRepositoryImpl(
    private val api: SpaceXApi,
    private val db: LaunchesDatabase,
    private val connectivityCheck: ConnectivityCheck
): LaunchesRepository {

    //private val dao = db.dao

    override suspend fun getRocketRef(rocketSearch: String): Result<RocketRef> {
        if (!connectivityCheck.isConnectedToNetwork()) {
            return Result.Error(NetworkError.PhoneOffline)
        }
        return withContext(Dispatchers.IO) {
            try {
                val apiResponse = api.requestRocketsRef()
                val rocketRef: RocketRefDto? = apiResponse.find { obj -> obj.name == rocketSearch }

                if (rocketRef == null) Result.Error(NetworkError.RocketRefNotFound)
                else Result.Success(rocketRef.toRocketRef())

            } catch (e: Exception) {
                Result.Error(NetworkError.Generic)
            }
        }
    }

    override suspend fun getLaunches(refresh: Boolean, id: String): Flow<Result<List<LaunchDetails>>> {
        return flow {
            emit(Result.Loading(true))

            val cache = db.dao.getLaunchDetails()
            cache.run {
                if (isEmpty() || refresh) {

                    val filterDataLambda: (LaunchDetailsDto) -> Boolean = { it.rocketId != null && it.rocketId == id && it.rocketName != null &&
                            it.date != null && it.success != null && it.media != null
                    }

                    var remoteData: List<LaunchDetailsDto>? = null
                    try {
                        remoteData = api.requestLaunches()
                    } catch (e: HttpException) {
                        emit(Result.Error(NetworkError.LaunchListFailure))
                        if(isEmpty()) return@flow
                        else return@run
                    } catch (e: Exception) {
                        emit(Result.Error(NetworkError.Generic))
                        if(isEmpty()) return@flow
                        else return@run
                    } finally { emit(Result.Loading(false)) }

                    remoteData?.let { dto ->
                        emit(Result.Loading(true))
                        db.dao.insertLaunchDetails(
                            dto.filter(filterDataLambda).map { it.toLaunchDetailsEntity() }
                        )
                    }
                }
            }
            emit(
                Result.Success(
                data = db.dao.getLaunchDetails().map {
                    it.toLaunchDetails()
                }
            ))
            emit(Result.Loading(false))
        }.flowOn(Dispatchers.IO)
    }
}
