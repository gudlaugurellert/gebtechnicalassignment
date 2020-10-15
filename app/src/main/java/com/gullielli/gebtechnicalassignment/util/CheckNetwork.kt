package com.gullielli.gebtechnicalassignment.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class CheckNetwork {

    // Probably overkill but nevertheless,
    // always a good idea to check if we have internet access...
    fun isNetworkAvail (context: Context) : Boolean {
        val connMgr = context.
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connMgr.activeNetwork ?: return false
            val activeNetwork = connMgr.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // old versions run this...
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }
}