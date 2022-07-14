package com.mariosodigie.apps.spacexdemoapp.launches.di

import com.mariosodigie.apps.spacexdemoapp.launches.ui.LaunchListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LaunchListViewModel(get()) }
}