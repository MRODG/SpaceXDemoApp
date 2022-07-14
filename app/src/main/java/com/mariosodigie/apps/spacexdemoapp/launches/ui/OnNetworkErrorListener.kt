package com.mariosodigie.apps.spacexdemoapp.launches.ui

import android.app.AlertDialog
import androidx.lifecycle.LiveData
import com.mariosodigie.apps.spacexdemoapp.launches.data.remote.NetworkError

interface OnNetworkErrorListener {
    var alertDialog: AlertDialog?
    fun addErrorSource(source: LiveData<NetworkError>)
    fun showErrorDialog(error: NetworkError)
}