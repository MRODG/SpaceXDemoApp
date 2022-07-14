package com.mariosodigie.apps.spacexdemoapp.launches

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mariosodigie.apps.globalapp.rules.MainCoroutineRule
import com.mariosodigie.apps.spacexdemoapp.launches.data.local.LaunchDetailsEntity
import com.mariosodigie.apps.spacexdemoapp.launches.domain.repository.LaunchesRepository
import com.mariosodigie.apps.spacexdemoapp.launches.ui.LaunchListViewModel
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.Result
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.LaunchDetails
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.RocketRef
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LaunchListViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var launchesRepository: LaunchesRepository

    private lateinit var launchListViewModel: LaunchListViewModel

    @Before
    fun setUp() {
        launchListViewModel = LaunchListViewModel(launchesRepository)
    }

    @Test
    fun getLaunchListTest() = runTest {

        val rocketRefResult = mock<Result.Success<RocketRef>>()
        whenever(launchesRepository.getRocketRef(any())).thenReturn(rocketRefResult)

        val rocketRef = mock<RocketRef>()
        whenever(rocketRefResult.data).thenReturn(rocketRef)
        whenever(rocketRef.id).thenReturn("abcd")


        val data = listOf(
            LaunchDetails(rocketName = "rocket", date = "2010-05-12", success = true, imageUrl = "https://some-image-url.com" ),
            LaunchDetails(rocketName = "rocket2", date = "2014-12-01", success = true, imageUrl = "https://some-image-url.com" ),
            LaunchDetails(rocketName = "rocket5", date = "2017-01-11", success = false, imageUrl = "https://some-image-url.com" ),
        )
        val getLaunchesResult = flow<Result<List<LaunchDetails>>>{emit(Result.Success(data))}
        whenever(launchesRepository.getLaunches(any(),any())).thenReturn(getLaunchesResult)

        val launchTitleObserver = mock<Observer<String>>()
        launchListViewModel.launchTitleLiveData.observeForever(launchTitleObserver)

        val launchDetailsObserver = mock<Observer<List<LaunchDetails>>>()
        launchListViewModel.launchDetailsLiveData.observeForever(launchDetailsObserver)

        launchListViewModel.getLaunchList()

        verify(launchesRepository).getRocketRef("Falcon 9")
        verify(launchesRepository).getLaunches(any(), any())
        verify(launchTitleObserver).onChanged(rocketRef.name)
        verify(launchDetailsObserver).onChanged(getLaunchesResult.first().data)
    }

}