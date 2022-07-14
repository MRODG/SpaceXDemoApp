package com.mariosodigie.apps.spacexdemoapp.launches.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<out T>.observe(owner: LifecycleOwner, callback: (T) -> Unit) {
    observe(owner, Observer {
        callback(it)
    })
}
