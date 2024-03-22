package com.sokol.pizzadreamadmin.FCM

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.SplashScreenActivity
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Common.updateToken(this, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val dataRemoteMessage = message.data
        if (dataRemoteMessage[Common.NOTIFICATION_TITLE] == "Нове замовлення")
        {
            val intent = Intent(this, SplashScreenActivity::class.java)
            intent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER, true)
            Common.showNotification(
                this,
                Random.nextInt(),
                dataRemoteMessage[Common.NOTIFICATION_TITLE],
                dataRemoteMessage[Common.NOTIFICATION_CONTENT],
                intent
            )
        }
        else {
            Common.showNotification(
                this,
                Random.nextInt(),
                dataRemoteMessage[Common.NOTIFICATION_TITLE],
                dataRemoteMessage[Common.NOTIFICATION_CONTENT],
                null
            )
        }
    }
}