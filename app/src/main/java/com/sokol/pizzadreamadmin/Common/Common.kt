package com.sokol.pizzadreamadmin.Common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.sokol.pizzadreamadmin.Model.AddonCategoryModel
import com.sokol.pizzadreamadmin.Model.CategoryModel
import com.sokol.pizzadreamadmin.Model.FoodModel
import com.sokol.pizzadreamadmin.Model.NewsModel
import com.sokol.pizzadreamadmin.Model.OrderModel
import com.sokol.pizzadreamadmin.Model.PizzeriaModel
import com.sokol.pizzadreamadmin.Model.SizeModel
import com.sokol.pizzadreamadmin.Model.TokenModel
import com.sokol.pizzadreamadmin.Model.UserModel
import com.sokol.pizzadreamadmin.Model.VacancyModel
import com.sokol.pizzadreamadmin.R
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
    val TOKEN_REF: String = "Tokens"

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
        "Підготовлено",
        "Готове до доставки",
        "В дорозі",
        "Доставлено",
        "Скасовано"
    )
    val NOTIFICATION_CONTENT: String = "content"
    val NOTIFICATION_TITLE = "title"

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

    fun updateToken(context: Context, token: String) {
        val tokenModel = TokenModel()
        tokenModel.email = currentUser!!.email
        tokenModel.token = token
        FirebaseDatabase.getInstance().getReference(TOKEN_REF).child(currentUser!!.uid)
            .setValue(tokenModel).addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun showNotification(
        context: Context, id: Int, title: String?, content: String?, intent: Intent?
    ) {
        var pendingIntent: PendingIntent? = null
        if (intent != null) {
            // Створення PendingIntent для запуску заданого Intent при натисканні на сповіщення
            pendingIntent =
                PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notificationChannelId = "sokol.pizzadream"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Створення каналу сповіщень, якщо працюємо на Android 8.0 або вище
            val notificationChannel = NotificationChannel(
                notificationChannelId, "Pizza Dream", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Pizza Dream"
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(context, notificationChannelId)
        builder.setContentTitle(title).setContentText(content).setAutoCancel(true)
            .setSmallIcon(R.drawable.icon)
        if (pendingIntent != null) {
            // Встановлення PendingIntent для сповіщення, яке виконується при натисканні на нього
            builder.setContentIntent(pendingIntent)
        }
        val notification = builder.build()
        // Відображення сповіщення
        notificationManager.notify(id, notification)
    }

    fun getNewOrderTopic(): String {
        return StringBuilder("/topics/new_order").toString()
    }
}