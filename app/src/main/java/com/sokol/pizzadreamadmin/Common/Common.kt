package com.sokol.pizzadreamadmin.Common

import android.content.Context
import android.net.ConnectivityManager
import com.sokol.pizzadreamadmin.Model.CategoryModel
import com.sokol.pizzadreamadmin.Model.NewsModel
import com.sokol.pizzadreamadmin.Model.UserModel
import com.sokol.pizzadreamadmin.Model.VacancyModel

object Common {
    val USER_REFERENCE: String = "Users"
    val CATEGORY_REF: String = "Categories"
    val PIZZERIA_REF: String = "Addresses"
    val NEWS_REF: String = "News"
    val VACANCIES_REF: String = "Vacancies"

    var currentUser: UserModel? = null
    var categorySelected: CategoryModel? = null
    var newsSelected: NewsModel? = null
    var vacancySelected: VacancyModel? = null
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