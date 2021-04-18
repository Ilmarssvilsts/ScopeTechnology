package com.example.maphw.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object Utils {
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}