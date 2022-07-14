package com.mariosodigie.apps.spacexdemoapp.launches.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariosodigie.apps.spacexdemoapp.BuildConfig
import com.mariosodigie.apps.spacexdemoapp.launches.domain.model.LaunchDetails
import com.mariosodigie.apps.spacexdemoapp.launches.domain.repository.LaunchesRepository
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.NetworkError
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.Result
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LaunchListViewModel (private val repository: LaunchesRepository): ViewModel() {

    private val _launchDetailsLiveData = MutableLiveData<List<LaunchDetails>>()
    val launchDetailsLiveData: LiveData<List<LaunchDetails>> = _launchDetailsLiveData

    private val _launchTitleLiveData = MutableLiveData<String>()
    val launchTitleLiveData: LiveData<String> = _launchTitleLiveData

    private val _networkError = MutableLiveData<NetworkError>()
    val networkError: LiveData<NetworkError> = _networkError

    private val _requestInProgress = MutableLiveData<Boolean>()
    val requestInProgress: LiveData<Boolean> = _requestInProgress

     fun getLaunchList(forceRefresh: Boolean= false, rockerName: String = BuildConfig.DEV_DEFAULT_ROCKET_SEARCH){
         viewModelScope.launch {
             var deferredLaunchList: Deferred<Flow<Result<List<LaunchDetails>>>>? = null

             val rocketIdResult = repository.getRocketRef(rockerName)
             when(rocketIdResult){
                 is Result.Success -> {
                     _launchTitleLiveData.value = rocketIdResult.data?.name
                     deferredLaunchList = async { repository.getLaunches(forceRefresh, rocketIdResult.data?.id!!) }

                 }
                 is Result.Error -> _networkError.value = rocketIdResult.error
                 else -> Unit
             }

             val launchListResult = deferredLaunchList?.await()
             launchListResult?.collect { result ->
                 when (result) {
                     is Result.Error -> {
                         _networkError.value = result.error!!
                     }
                     is Result.Success -> {
                         _launchDetailsLiveData.value = result.data
                     }
                     is Result.Loading -> _requestInProgress.value = result.isLoading
                 }
             }
         }
    }
}