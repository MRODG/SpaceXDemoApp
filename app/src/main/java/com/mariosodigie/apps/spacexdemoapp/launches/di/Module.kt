package com.mariosodigie.apps.spacexdemoapp.launches.di

import androidx.room.Room
import com.mariosodigie.apps.spacexdemoapp.launches.data.local.LaunchesDatabase
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.SpaceXApi
import com.mariosodigie.apps.spacexdemoapp.launches.data.repository.LaunchesRepositoryImpl
import com.mariosodigie.apps.spacexdemoapp.launches.domain.repository.LaunchesRepository
import com.mariosodigie.apps.spacexdemoapp.launches.utis.ConnectivityCheck
import org.koin.dsl.module

val appModule = module {
    single { ConnectivityCheck(get()) }
    single <LaunchesRepository> { LaunchesRepositoryImpl(SpaceXApi.get(), get(), get() ) }
    single {
        Room.databaseBuilder(
            get(),
            LaunchesDatabase::class.java, "launches.db"
        ).build()
    }
}
