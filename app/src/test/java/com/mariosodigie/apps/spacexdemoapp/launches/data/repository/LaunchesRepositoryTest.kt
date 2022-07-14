package com.mariosodigie.apps.spacexdemoapp.launches.data.repository

import com.mariosodigie.apps.globalapp.rules.MainCoroutineRule
import com.mariosodigie.apps.spacexdemoapp.launches.data.local.LaunchDetailsEntity
import com.mariosodigie.apps.spacexdemoapp.launches.data.local.LaunchesDao
import com.mariosodigie.apps.spacexdemoapp.launches.data.local.LaunchesDatabase
import com.mariosodigie.apps.spacexdemoapp.launches.data.mapper.toLaunchDetails
import com.mariosodigie.apps.spacexdemoapp.launches.data.mapper.toLaunchDetailsEntity
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.NetworkError
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.Result
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.SpaceXApi
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.LaunchDetailsDto
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.dto.RocketRefDto
import com.mariosodigie.apps.spacexdemoapp.launches.utis.ConnectivityCheck
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LaunchesRepositoryTest {

    private lateinit var launchesRepositoryImpl: LaunchesRepositoryImpl

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var connectivityCheck: ConnectivityCheck

    @Mock
    lateinit var api: SpaceXApi

    @Mock
    lateinit var db: LaunchesDatabase

    @Before
    fun setUp() {
        launchesRepositoryImpl = LaunchesRepositoryImpl(api, db, connectivityCheck)
    }

    @Test
    fun rocketRefSuccessfullyRequested() = runTest {

        val data = listOf(RocketRefDto(name = "rocket", id = "abcd" ),
            RocketRefDto(name = "rocket2", id = "efgh" ),
            RocketRefDto(name = "rocket3", id = "ijkl" )
        )

        whenever(connectivityCheck.isConnectedToNetwork()).thenReturn(true)
        whenever(api.requestRocketsRef()).thenReturn(data)

        val result = launchesRepositoryImpl.getRocketRef("rocket2")

        verify(api).requestRocketsRef()

        assert(result is Result.Success)
        assertEquals("efgh", result.data!!.id)
        assertEquals("rocket2", result.data!!.name,)
    }

    @Test
    fun rocketRefNotFound() = runTest {
        val data = listOf(RocketRefDto(name = "rocket", id = "abcd" ),
            RocketRefDto(name = "rocket2", id = "efgh" ),
            RocketRefDto(name = "rocket3", id = "ijkl" )
        )

        whenever(connectivityCheck.isConnectedToNetwork()).thenReturn(true)
        whenever(api.requestRocketsRef()).thenReturn(data)

        val result = launchesRepositoryImpl.getRocketRef("rocket5")

        verify(api).requestRocketsRef()

        assert(result is Result.Error)
        assertEquals(NetworkError.RocketRefNotFound, result.error)
    }

    @Test
    fun getRocketRefFailsWhenPhoneIsOffline() = runTest {
        val data = listOf(RocketRefDto(name = "rocket", id = "abcd" ),
            RocketRefDto(name = "rocket2", id = "efgh" ),
            RocketRefDto(name = "rocket3", id = "ijkl" )
        )

        whenever(connectivityCheck.isConnectedToNetwork()).thenReturn(false)
        whenever(api.requestRocketsRef()).thenReturn(data)

        val result = launchesRepositoryImpl.getRocketRef("rocket5")

        assert(result is Result.Error)
        assertEquals(NetworkError.PhoneOffline, result.error)
    }

    @Test
    fun launchesSuccessfullyRequestedFromDB() = runTest {

        val data = listOf(
            LaunchDetailsEntity(rocketName = "rocket", date = "2010-05-12", success = true, imageUrl = "https://some-image-url.com" ),
            LaunchDetailsEntity(rocketName = "rocket2", date = "2014-12-01", success = true, imageUrl = "https://some-image-url.com" ),
            LaunchDetailsEntity(rocketName = "rocket5", date = "2017-01-11", success = false, imageUrl = "https://some-image-url.com" ),
        )

        val daoMock = mock<LaunchesDao>()
        whenever(db.dao).thenReturn(daoMock)
        whenever(daoMock.getLaunchDetails()).thenReturn(data)

        val flowEmissions = launchesRepositoryImpl.getLaunches(false, "").toList()

        verify(daoMock).getLaunchDetails()
        assertEquals(3, flowEmissions.size)

        assert(flowEmissions[0] is Result.Loading)
        assert(flowEmissions[1] is Result.Success)
        assert(flowEmissions[2] is Result.Loading)

        assertEquals(data.map { it.toLaunchDetails() }, flowEmissions[1].data)
    }

    @Test
    fun launchesSuccessfullyRequestedFromRemoteAndReplaceLocalData() = runTest {

        val targetRocketId = "efgh"
        val remoteData = listOf(
            LaunchDetailsDto(rocketId = "abcd", rocketName = "remoteRocket", date = "2020-06-13T09:21:00.000Z", success = true, media = LaunchDetailsDto.Links(patch = LaunchDetailsDto.Links.Patch(small = "https://some-image-url-small.com", large = "https://some-image-url-large.com"))),
            LaunchDetailsDto(rocketId = targetRocketId, rocketName = "remoteRocket2", date = "2014-12-01T13:25:00.000Z", success = true, media = LaunchDetailsDto.Links(patch = LaunchDetailsDto.Links.Patch(small = "https://some-image-url-small.com", large = "https://some-image-url-large.com")))
        )

        val localData = listOf(
            LaunchDetailsEntity(rocketName = "localRocket", date = "2010-05-12T09:21:00.000Z", success = true, imageUrl = "https://some-image-url-small.com" ),
            LaunchDetailsEntity(rocketName = "localRocket2", date = "2014-12-01T13:25:00.000Z", success = true, imageUrl = "https://some-image-url-small.com" )
        )

        val daoMock = mock<LaunchesDao>()
        whenever(db.dao).thenReturn(daoMock)
        whenever(daoMock.getLaunchDetails()).thenReturn(localData)
        whenever(daoMock.insertLaunchDetails(any())).thenReturn(Unit)
        whenever(api.requestLaunches()).thenReturn(remoteData)

        val flowEmissions = launchesRepositoryImpl.getLaunches(true, targetRocketId).toList()

        verify(api).requestLaunches()
        verify(daoMock).insertLaunchDetails(remoteData.filter { it.rocketId == targetRocketId }.map { it.toLaunchDetailsEntity() })
        verify(daoMock, times(2)).getLaunchDetails()


        assertEquals(5, flowEmissions.size)

        assert(flowEmissions[0] is Result.Loading)
        assert(flowEmissions[3] is Result.Success)
        assert(flowEmissions[4] is Result.Loading)
    }
}