package com.mariosodigie.apps.spacexdemoapp

import android.app.Application
import com.mariosodigie.apps.spacexdemoapp.launches.di.viewModelModule
import com.mariosodigie.apps.spacexdemoapp.launches.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SpaceXApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@SpaceXApp)
            modules(listOf(appModule, viewModelModule))
        }
    }
}