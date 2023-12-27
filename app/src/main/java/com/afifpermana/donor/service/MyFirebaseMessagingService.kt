package com.afifpermana.donor.service

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.INotificationSideChannel
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.afifpermana.donor.ChatActivity
import com.afifpermana.donor.MainActivity
import com.afifpermana.donor.R
import com.afifpermana.donor.SplashScreenActivity
import com.afifpermana.donor.model.HomeResponse
import com.afifpermana.donor.model.SendTokenFCMToServerResponse
import com.afifpermana.donor.util.Retro
import com.afifpermana.donor.util.SharedPrefLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("tokenden", token.toString())
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Dikirim dari: ${remoteMessage.from}")
        // Mendapatkan data notifikasi
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        // Mendapatkan data tambahan dari payload
        val additionalData = remoteMessage.data

        Log.d(TAG, "Data tambahan: $additionalData")


        // Menampilkan notifikasi
//        showNotification(title, body, additionalData)


    }

    fun showNotification(title: String?, body: String?, additionalData: Map<String, String>) {
        // Intent default untuk membuka MainActivity
        val defaultIntent = Intent(this, SplashScreenActivity::class.java)

        if (additionalData.isNotEmpty()) {
            for ((key, value) in additionalData) {
                defaultIntent.putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, defaultIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}