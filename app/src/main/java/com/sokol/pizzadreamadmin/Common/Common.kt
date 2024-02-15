package com.sokol.pizzadreamadmin.Common

import android.content.Context
import android.net.ConnectivityManager
import com.sokol.pizzadreamadmin.Model.UserModel

object Common {
    val USER_REFERENCE: String = "Users"
    var currentUser: UserModel? = null
    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            // Доступне підключення до Інтернету
            return true
        }
        // Немає підключення до Інтернету
        return false
    }
}