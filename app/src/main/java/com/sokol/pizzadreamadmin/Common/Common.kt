package com.sokol.pizzadreamadmin.Common

import android.content.Context
import android.net.ConnectivityManager
import com.sokol.pizzadreamadmin.Model.AddonCategoryModel
import com.sokol.pizzadreamadmin.Model.CategoryModel
import com.sokol.pizzadreamadmin.Model.FoodModel
import com.sokol.pizzadreamadmin.Model.NewsModel
import com.sokol.pizzadreamadmin.Model.OrderModel
import com.sokol.pizzadreamadmin.Model.PizzeriaModel
import com.sokol.pizzadreamadmin.Model.SizeModel
import com.sokol.pizzadreamadmin.Model.UserModel
import com.sokol.pizzadreamadmin.Model.VacancyModel
import java.math.RoundingMode
import java.text.DecimalFormat

object Common {
    val USER_REFERENCE: String = "Users"
    val CATEGORY_REF: String = "Categories"
    val PIZZERIA_REF: String = "Addresses"
    val NEWS_REF: String = "News"
    val VACANCIES_REF: String = "Vacancies"
    val REVIEWS_REF = "ReviewsPizzeria"
    val RESUMES_REF = "Resumes"
    val ADDON_CATEGORY_REF = "Addon"
    val COMMENT_REF = "Comments"
    val ORDER_REF = "Orders"

    var currentUser: UserModel? = null
    var categorySelected: CategoryModel? = null
    var newsSelected: NewsModel? = null
    var vacancySelected: VacancyModel? = null
    var pizzeriaSelected: PizzeriaModel? = null
    var foodSelected: FoodModel? = null
    var addonCategorySelected: AddonCategoryModel? = null
    var orderSelected: OrderModel? = null
    var sizeSelected: List<SizeModel> = ArrayList()
    var STATUSES: List<String> = listOf(
        "Очікує підтвердження",
        "Підготовка",
        "Готовий до доставки",
        "В дорозі",
        "Доставлено",
        "Скасовано"
    )
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
    fun formatPrice(price: Double): String {
        if (price != 0.toDouble()) {
            val df = DecimalFormat("#,##0.00")
            df.roundingMode = RoundingMode.HALF_UP
            val finalPrice = StringBuilder(df.format(price).toString() + " грн.").toString()
            return finalPrice
        } else {
            return "0,00 грн."
        }
    }
}